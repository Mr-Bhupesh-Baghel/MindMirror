package com.mindmirror.backend.pushups.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mindmirror.backend.pushups.dto.PushupChallengeHistoryResponse;
import com.mindmirror.backend.pushups.dto.PushupChallengeRequest;
import com.mindmirror.backend.pushups.dto.PushupChallengeResponse;
import com.mindmirror.backend.pushups.dto.PushupMaintenanceHistoryResponse;
import com.mindmirror.backend.pushups.dto.PushupMaintenanceRequest;
import com.mindmirror.backend.pushups.dto.PushupMaintenanceResponse;
import com.mindmirror.backend.pushups.entity.MaintenanceEntry;
import com.mindmirror.backend.pushups.entity.PushupEntry;
import com.mindmirror.backend.pushups.entity.PushupStatus;
import com.mindmirror.backend.pushups.repository.MaintenanceRepository;
import com.mindmirror.backend.pushups.repository.PushupRepository;
import com.mindmirror.backend.user.entity.User;

@Service
public class PushupService {

    private static final int CHALLENGE_TOTAL_DAYS = 100;

    private final PushupRepository pushupRepository;
    private final MaintenanceRepository maintenanceRepository;

    public PushupService(PushupRepository pushupRepository, MaintenanceRepository maintenanceRepository) {
        this.pushupRepository = pushupRepository;
        this.maintenanceRepository = maintenanceRepository;
    }

    @Transactional(readOnly = true)
    public PushupChallengeResponse getChallenge(User user) {
        return pushupRepository.findFirstByUserOrderByChallengeDayDesc(user)
            .map(entry -> challengeResponse(entry, challengeStats(user)))
            .orElseGet(this::emptyChallengeResponse);
    }

    @Transactional
    public PushupChallengeResponse saveChallenge(User user, PushupChallengeRequest request) {
        int targetCount = request.getTargetCount() == null ? request.getChallengeDay() : request.getTargetCount();

        PushupEntry entry = pushupRepository.findByUserAndChallengeDay(user, request.getChallengeDay())
            .orElseGet(PushupEntry::new);
        entry.setUser(user);
        entry.setChallengeDay(request.getChallengeDay());
        entry.setEntryDate(request.getEntryDate());
        entry.setTargetCount(targetCount);
        entry.setCompletedCount(request.getCompletedCount());
        entry.setStatus(request.getCompletedCount() >= targetCount ? PushupStatus.DONE : PushupStatus.IN_PROGRESS);

        PushupEntry saved = pushupRepository.save(entry);
        return challengeResponse(saved, challengeStats(user));
    }

    @Transactional(readOnly = true)
    public List<PushupChallengeHistoryResponse> challengeHistory(User user) {
        return pushupRepository.findByUserOrderByChallengeDayDesc(user)
            .stream()
            .map(this::toChallengeHistoryResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public PushupMaintenanceResponse getMaintenance(User user) {
        return maintenanceRepository.findFirstByUserOrderByEntryDateDesc(user)
            .map(entry -> maintenanceResponse(entry, maintenanceStats(user)))
            .orElseGet(this::emptyMaintenanceResponse);
    }

    @Transactional
    public PushupMaintenanceResponse saveMaintenance(User user, PushupMaintenanceRequest request) {
        MaintenanceEntry entry = maintenanceRepository.findByUserAndEntryDate(user, request.getEntryDate())
            .orElseGet(MaintenanceEntry::new);
        entry.setUser(user);
        entry.setEntryDate(request.getEntryDate());
        entry.setPushupsCount(request.getPushupsCount());
        entry.setChallengeDay(request.getChallengeDay());

        MaintenanceEntry saved = maintenanceRepository.save(entry);
        return maintenanceResponse(saved, maintenanceStats(user));
    }

    @Transactional(readOnly = true)
    public List<PushupMaintenanceHistoryResponse> maintenanceHistory(User user) {
        return maintenanceRepository.findByUserOrderByEntryDateDesc(user)
            .stream()
            .map(this::toMaintenanceHistoryResponse)
            .toList();
    }

    private PushupChallengeResponse challengeResponse(PushupEntry entry, PushupChallengeResponse stats) {
        PushupChallengeResponse response = stats;
        response.setEntryDate(entry.getEntryDate());
        response.setChallengeDay(entry.getChallengeDay());
        response.setTargetCount(entry.getTargetCount());
        response.setCompletedCount(entry.getCompletedCount());
        response.setStatus(entry.getStatus());
        response.setChallengeComplete(response.getCompletedDays() >= CHALLENGE_TOTAL_DAYS);
        return response;
    }

    private PushupChallengeResponse emptyChallengeResponse() {
        PushupChallengeResponse response = new PushupChallengeResponse();
        response.setChallengeComplete(false);
        return response;
    }

    private PushupMaintenanceResponse maintenanceResponse(MaintenanceEntry entry, PushupMaintenanceResponse stats) {
        PushupMaintenanceResponse response = stats;
        response.setEntryDate(entry.getEntryDate());
        response.setPushupsCount(entry.getPushupsCount());
        response.setChallengeDay(entry.getChallengeDay());
        return response;
    }

    private PushupMaintenanceResponse emptyMaintenanceResponse() {
        return new PushupMaintenanceResponse();
    }

    private PushupChallengeHistoryResponse toChallengeHistoryResponse(PushupEntry entry) {
        PushupChallengeHistoryResponse response = new PushupChallengeHistoryResponse();
        response.setEntryDate(entry.getEntryDate());
        response.setChallengeDay(entry.getChallengeDay());
        response.setTargetCount(entry.getTargetCount());
        response.setCompletedCount(entry.getCompletedCount());
        response.setStatus(entry.getStatus());
        response.setChallengeComplete(isChallengeComplete(entry));
        return response;
    }

    private PushupMaintenanceHistoryResponse toMaintenanceHistoryResponse(MaintenanceEntry entry) {
        PushupMaintenanceHistoryResponse response = new PushupMaintenanceHistoryResponse();
        response.setEntryDate(entry.getEntryDate());
        response.setPushupsCount(entry.getPushupsCount());
        response.setChallengeDay(entry.getChallengeDay());
        return response;
    }

    private PushupChallengeResponse challengeStats(User user) {
        List<PushupEntry> entries = pushupRepository.findByUserOrderByChallengeDayDesc(user);
        List<PushupEntry> ascendingEntries = entries.stream()
            .sorted(Comparator.comparing(PushupEntry::getChallengeDay))
            .toList();

        int completedDays = (int) entries.stream()
            .filter(this::isChallengeComplete)
            .count();
        int totalTargetPushups = entries.stream().mapToInt(PushupEntry::getTargetCount).sum();
        int totalCompletedPushups = entries.stream().mapToInt(PushupEntry::getCompletedCount).sum();

        PushupChallengeResponse response = new PushupChallengeResponse();
        response.setTotalDays(entries.size());
        response.setCompletedDays(completedDays);
        response.setTotalTargetPushups(totalTargetPushups);
        response.setTotalCompletedPushups(totalCompletedPushups);
        response.setCompletionRate(percentage(completedDays, entries.size()));
        response.setCurrentStreak(calculateCurrentChallengeStreak(ascendingEntries));
        response.setLongestStreak(calculateLongestChallengeStreak(ascendingEntries));
        response.setChallengeComplete(completedDays >= CHALLENGE_TOTAL_DAYS);
        return response;
    }

    private PushupMaintenanceResponse maintenanceStats(User user) {
        List<MaintenanceEntry> entries = maintenanceRepository.findByUserOrderByEntryDateDesc(user);
        List<MaintenanceEntry> ascendingEntries = entries.stream()
            .sorted(Comparator.comparing(MaintenanceEntry::getEntryDate))
            .toList();

        int totalPushups = entries.stream().mapToInt(MaintenanceEntry::getPushupsCount).sum();

        PushupMaintenanceResponse response = new PushupMaintenanceResponse();
        response.setTotalDays(entries.size());
        response.setTotalPushups(totalPushups);
        response.setAveragePerDay(average(totalPushups, entries.size()));
        response.setCurrentStreak(calculateCurrentMaintenanceStreak(ascendingEntries));
        response.setLongestStreak(calculateLongestMaintenanceStreak(ascendingEntries));
        return response;
    }

    private boolean isChallengeComplete(PushupEntry entry) {
        return entry.getCompletedCount() >= entry.getTargetCount() && entry.getStatus() == PushupStatus.DONE;
    }

    private int calculateCurrentChallengeStreak(List<PushupEntry> ascendingEntries) {
        if (ascendingEntries.isEmpty()) {
            return 0;
        }

        int streak = 0;
        Integer expectedDay = ascendingEntries.get(ascendingEntries.size() - 1).getChallengeDay();

        for (int i = ascendingEntries.size() - 1; i >= 0; i--) {
            PushupEntry entry = ascendingEntries.get(i);
            if (!Objects.equals(entry.getChallengeDay(), expectedDay) || !isChallengeComplete(entry)) {
                break;
            }

            streak++;
            expectedDay = expectedDay - 1;
        }

        return streak;
    }

    private int calculateLongestChallengeStreak(List<PushupEntry> ascendingEntries) {
        int longest = 0;
        int current = 0;
        Integer previousCompletedDay = null;

        for (PushupEntry entry : ascendingEntries) {
            if (!isChallengeComplete(entry)) {
                current = 0;
                previousCompletedDay = null;
                continue;
            }

            if (previousCompletedDay != null && previousCompletedDay + 1 == entry.getChallengeDay()) {
                current++;
            } else {
                current = 1;
            }

            longest = Math.max(longest, current);
            previousCompletedDay = entry.getChallengeDay();
        }

        return longest;
    }

    private int calculateCurrentMaintenanceStreak(List<MaintenanceEntry> ascendingEntries) {
        LocalDate expectedDate = LocalDate.now();
        int streak = 0;

        for (int i = ascendingEntries.size() - 1; i >= 0; i--) {
            MaintenanceEntry entry = ascendingEntries.get(i);
            if (entry.getEntryDate().isAfter(expectedDate)) {
                continue;
            }
            if (!entry.getEntryDate().equals(expectedDate)) {
                break;
            }

            streak++;
            expectedDate = expectedDate.minusDays(1);
        }

        return streak;
    }

    private int calculateLongestMaintenanceStreak(List<MaintenanceEntry> ascendingEntries) {
        int longest = 0;
        int current = 0;
        LocalDate previousDate = null;

        for (MaintenanceEntry entry : ascendingEntries) {
            if (previousDate != null && previousDate.plusDays(1).equals(entry.getEntryDate())) {
                current++;
            } else {
                current = 1;
            }

            longest = Math.max(longest, current);
            previousDate = entry.getEntryDate();
        }

        return longest;
    }

    private double percentage(int numerator, int denominator) {
        if (denominator == 0) {
            return 0;
        }

        return BigDecimal.valueOf(numerator)
            .multiply(BigDecimal.valueOf(100))
            .divide(BigDecimal.valueOf(denominator), 2, RoundingMode.HALF_UP)
            .doubleValue();
    }

    private double average(int totalPushups, int totalDays) {
        if (totalDays == 0) {
            return 0;
        }

        return BigDecimal.valueOf(totalPushups)
            .divide(BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP)
            .doubleValue();
    }
}
