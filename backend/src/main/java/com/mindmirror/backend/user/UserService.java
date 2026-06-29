package com.mindmirror.backend.user;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.mindmirror.backend.auth.RefreshTokenService;
import com.mindmirror.backend.exception.ApiException;
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

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional(readOnly = true)
    public UserResponse getProfile(User user) {
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateProfile(User user, UpdateUserRequest request) {
        if (StringUtils.hasText(request.displayName())) {
            user.setDisplayName(request.displayName().trim());
        }

        if (StringUtils.hasText(request.newPassword())) {
            if (!StringUtils.hasText(request.currentPassword())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Current password is required to change password");
            }
            if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
            }
            user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
            refreshTokenService.revokeAll(user);
        }

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public void deleteAccount(User user) {
        refreshTokenService.revokeAll(user);
        user.setStatus(UserStatus.DELETED);
        user.setEmail("deleted-" + user.getId() + "@deleted.local");
        userRepository.save(user);
    }
}
