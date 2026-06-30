package com.mindmirror.backend.water.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;

import com.mindmirror.backend.exception.ApiException;
import com.mindmirror.backend.user.entity.User;
import com.mindmirror.backend.water.dto.WaterHistoryResponse;
import com.mindmirror.backend.water.dto.WaterRequest;
import com.mindmirror.backend.water.dto.WaterResponse;
import com.mindmirror.backend.water.dto.WaterStatsResponse;
import com.mindmirror.backend.water.entity.WaterEntry;
import com.mindmirror.backend.water.repository.WaterRepository;

@Service
public class WaterService {

    private static final int DEFAULT_GOAL_GLASSES = 8;

    private final WaterRepository waterRepository;

    public WaterService(WaterRepository waterRepository) {
        this.waterRepository = waterRepository;
    }

    @Transactional(readOnly = true)
    public WaterResponse getByDate(User user, LocalDate date) {
        return waterRepository.findByUserAndEntryDate(user, date)
            .map(this::toResponse)
            .orElseGet(() -> emptyResponse(date));
    }

    @Transactional
    public WaterResponse save(User user, WaterRequest request) {
        WaterEntry entry = waterRepository.findByUserAndEntryDate(user, request.getEntryDate())
            .orElseGet(() -> newEntry(user, request.getEntryDate()));

        entry.setGlassesCount(request.getGlasses());
        if (request.getGoalGlasses() != null) {
            entry.setGoalGlasses(request.getGoalGlasses());
        }

        return toResponse(waterRepository.save(entry));
    }

    @Transactional(readOnly = true)
    public List<WaterHistoryResponse> history(User user, LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "from must be on or before to");
        }

        List<WaterEntry> entries = from == null && to == null
            ? waterRepository.findByUserOrderByEntryDateDesc(user)
            : waterRepository.findByUserAndEntryDateBetweenOrderByEntryDateDesc(
                user,
                from == null ? LocalDate.of(1000, 1, 1) : from,
                to == null ? LocalDate.of(9999, 12, 31) : to
            );

        return entries.stream()
            .map(this::toHistoryResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public WaterStatsResponse stats(User user) {
        List<WaterEntry> entries = waterRepository.findByUserOrderByEntryDateDesc(user);
        List<WaterEntry> ascendingEntries = entries.stream()
            .sorted(Comparator.comparing(WaterEntry::getEntryDate))
            .toList();

        int totalGlasses = entries.stream()
            .mapToInt(WaterEntry::getGlassesCount)
            .sum();
        int goalDays = (int) entries.stream()
            .filter(this::isGoalMet)
            .count();

        WaterStatsResponse response = new WaterStatsResponse();
        response.setTotalDays(entries.size());
        response.setGoalDays(goalDays);
        response.setTotalGlasses(totalGlasses);
        response.setCurrentStreak(calculateCurrentStreak(ascendingEntries));
        response.setLongestStreak(calculateLongestStreak(ascendingEntries));
        response.setAveragePerDay(average(totalGlasses, entries.size()));
        return response;
    }

    private WaterEntry newEntry(User user, LocalDate entryDate) {
        WaterEntry entry = new WaterEntry();
        entry.setUser(user);
        entry.setEntryDate(entryDate);
        entry.setGoalGlasses(DEFAULT_GOAL_GLASSES);
        return entry;
    }

    private WaterResponse toResponse(WaterEntry entry) {
        WaterResponse response = new WaterResponse();
        response.setEntryDate(entry.getEntryDate());
        response.setGlasses(entry.getGlassesCount());
        response.setGoalGlasses(entry.getGoalGlasses());
        response.setGoalMet(isGoalMet(entry));
        return response;
    }

    private WaterHistoryResponse toHistoryResponse(WaterEntry entry) {
        WaterHistoryResponse response = new WaterHistoryResponse();
        response.setEntryDate(entry.getEntryDate());
        response.setGlasses(entry.getGlassesCount());
        response.setGoalGlasses(entry.getGoalGlasses());
        response.setGoalMet(isGoalMet(entry));
        return response;
    }

    private WaterResponse emptyResponse(LocalDate date) {
        WaterResponse response = new WaterResponse();
        response.setEntryDate(date);
        response.setGlasses(0);
        response.setGoalGlasses(DEFAULT_GOAL_GLASSES);
        response.setGoalMet(false);
        return response;
    }

    private boolean isGoalMet(WaterEntry entry) {
        return entry.getGlassesCount() >= entry.getGoalGlasses();
    }

    private int calculateCurrentStreak(List<WaterEntry> ascendingEntries) {
        LocalDate expectedDate = LocalDate.now();
        int streak = 0;

        for (int i = ascendingEntries.size() - 1; i >= 0; i--) {
            WaterEntry entry = ascendingEntries.get(i);
            if (entry.getEntryDate().isAfter(expectedDate)) {
                continue;
            }
            if (!entry.getEntryDate().equals(expectedDate) || !isGoalMet(entry)) {
                break;
            }

            streak++;
            expectedDate = expectedDate.minusDays(1);
        }

        return streak;
    }

    private int calculateLongestStreak(List<WaterEntry> ascendingEntries) {
        int longest = 0;
        int current = 0;
        Optional<LocalDate> previousDate = Optional.empty();

        for (WaterEntry entry : ascendingEntries) {
            if (!isGoalMet(entry)) {
                current = 0;
                previousDate = Optional.of(entry.getEntryDate());
                continue;
            }

            if (previousDate.isPresent() && previousDate.get().plusDays(1).equals(entry.getEntryDate())) {
                current++;
            } else {
                current = 1;
            }

            longest = Math.max(longest, current);
            previousDate = Optional.of(entry.getEntryDate());
        }

        return longest;
    }

    private double average(int totalGlasses, int totalDays) {
        if (totalDays == 0) {
            return 0;
        }

        return BigDecimal.valueOf(totalGlasses)
            .divide(BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP)
            .doubleValue();
    }
}
