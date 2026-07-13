package com.mindmirror.backend.user;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.mindmirror.backend.auth.RefreshTokenService;
import com.mindmirror.backend.exception.ApiException;
import com.mindmirror.backend.pushups.repository.MaintenanceRepository;
import com.mindmirror.backend.pushups.repository.PushupRepository;
import com.mindmirror.backend.routine.repository.RoutineCompletionRepository;
import com.mindmirror.backend.water.repository.WaterRepository;
import com.mindmirror.backend.user.dto.AccountSummaryResponse;
import com.mindmirror.backend.user.dto.UpdateUserRequest;
import com.mindmirror.backend.user.dto.UserResponse;
import com.mindmirror.backend.user.entity.User;
import com.mindmirror.backend.user.entity.UserStatus;
import com.mindmirror.backend.user.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final WaterRepository waterRepository;
    private final PushupRepository pushupRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final RoutineCompletionRepository routineCompletionRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService,
                       WaterRepository waterRepository, PushupRepository pushupRepository,
                       MaintenanceRepository maintenanceRepository, RoutineCompletionRepository routineCompletionRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.waterRepository = waterRepository;
        this.pushupRepository = pushupRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.routineCompletionRepository = routineCompletionRepository;
    }

    @Transactional(readOnly = true)
    public UserResponse getProfile(User user) {
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateProfile(User user, UpdateUserRequest request) {
        boolean emailChanged = StringUtils.hasText(request.email())
            && !normalizeEmail(request.email()).equals(user.getEmail());
        boolean passwordChanged = StringUtils.hasText(request.newPassword());

        if ((emailChanged || passwordChanged) && !StringUtils.hasText(request.currentPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Current password is required to change email or password");
        }
        if ((emailChanged || passwordChanged) && !passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }

        if (StringUtils.hasText(request.displayName())) {
            user.setDisplayName(request.displayName().trim());
        }

        if (emailChanged) {
            String email = normalizeEmail(request.email());
            if (userRepository.existsByEmailIgnoreCaseAndIdNot(email, user.getId())) {
                throw new ApiException(HttpStatus.CONFLICT, "Email is already registered");
            }
            user.setEmail(email);
        }

        if (passwordChanged) {
            user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
            refreshTokenService.revokeAll(user);
        }

        if (request.darkMode() != null) {
            user.setDarkMode(request.darkMode());
        }
        if (request.notifications() != null) {
            user.setNotificationsEnabled(request.notifications());
        }

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public AccountSummaryResponse getAccountSummary(User user) {
        long routineDays = routineCompletionRepository.countByUserAndCompletedTrue(user);
        int waterGlasses = waterRepository.findByUserOrderByEntryDateDesc(user).stream()
            .mapToInt(entry -> entry.getGlassesCount()).sum();
        int pushups = pushupRepository.findByUserOrderByChallengeDayDesc(user).stream()
            .mapToInt(entry -> entry.getCompletedCount()).sum()
            + maintenanceRepository.findByUserOrderByEntryDateDesc(user).stream()
                .mapToInt(entry -> entry.getPushupsCount()).sum();
        Set<LocalDate> activityDates = new HashSet<>();
        waterRepository.findByUserOrderByEntryDateDesc(user).forEach(entry -> activityDates.add(entry.getEntryDate()));
        pushupRepository.findByUserOrderByChallengeDayDesc(user).forEach(entry -> activityDates.add(entry.getEntryDate()));
        maintenanceRepository.findByUserOrderByEntryDateDesc(user).forEach(entry -> activityDates.add(entry.getEntryDate()));
        routineCompletionRepository.findByUserAndCompletedTrue(user).forEach(entry -> activityDates.add(entry.getCompletionDate()));
        return new AccountSummaryResponse(routineDays, waterGlasses, pushups, currentStreak(activityDates),
            user.isDarkMode(), user.isNotificationsEnabled());
    }

    private int currentStreak(Set<LocalDate> dates) {
        int streak = 0;
        LocalDate cursor = LocalDate.now(ZoneOffset.UTC);
        while (dates.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    @Transactional
    public void deleteAccount(User user) {
        refreshTokenService.revokeAll(user);
        user.setStatus(UserStatus.DELETED);
        user.setEmail("deleted-" + user.getId() + "@deleted.local");
        userRepository.save(user);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
