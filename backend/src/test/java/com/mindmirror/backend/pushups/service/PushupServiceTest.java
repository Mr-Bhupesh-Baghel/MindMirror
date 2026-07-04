package com.mindmirror.backend.pushups.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mindmirror.backend.pushups.dto.PushupChallengeRequest;
import com.mindmirror.backend.pushups.dto.PushupChallengeResponse;
import com.mindmirror.backend.pushups.dto.PushupMaintenanceRequest;
import com.mindmirror.backend.pushups.dto.PushupMaintenanceResponse;
import com.mindmirror.backend.pushups.entity.MaintenanceEntry;
import com.mindmirror.backend.pushups.entity.PushupEntry;
import com.mindmirror.backend.pushups.entity.PushupStatus;
import com.mindmirror.backend.pushups.repository.MaintenanceRepository;
import com.mindmirror.backend.pushups.repository.PushupRepository;
import com.mindmirror.backend.user.entity.User;

@ExtendWith(MockitoExtension.class)
class PushupServiceTest {

    @Mock
    private PushupRepository pushupRepository;

    @Mock
    private MaintenanceRepository maintenanceRepository;

    @Test
    void saveChallengeUpdatesExistingDayAndComputesStatistics() {
        User user = new User();
        LocalDate date = LocalDate.of(2026, 7, 1);
        PushupEntry existingEntry = challengeEntry(user, date, 12, 8, 8, PushupStatus.DONE);
        PushupChallengeRequest request = challengeRequest(date, 12, 12, 12);

        when(pushupRepository.findByUserAndChallengeDay(user, 12)).thenReturn(Optional.of(existingEntry));
        when(pushupRepository.findByUserOrderByChallengeDayDesc(user)).thenReturn(List.of(
            challengeEntry(user, date, 12, 12, 12, PushupStatus.DONE),
            challengeEntry(user, date.minusDays(1), 11, 7, 11, PushupStatus.DONE),
            challengeEntry(user, date.minusDays(2), 10, 10, 10, PushupStatus.DONE),
            challengeEntry(user, date.minusDays(3), 9, 5, 9, PushupStatus.IN_PROGRESS)
        ));
        when(pushupRepository.save(any(PushupEntry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PushupService service = new PushupService(pushupRepository, maintenanceRepository);
        PushupChallengeResponse response = service.saveChallenge(user, request);

        ArgumentCaptor<PushupEntry> captor = ArgumentCaptor.forClass(PushupEntry.class);
        verify(pushupRepository).save(captor.capture());

        assertThat(captor.getValue()).isSameAs(existingEntry);
        assertThat(response.getChallengeDay()).isEqualTo(12);
        assertThat(response.getTargetCount()).isEqualTo(12);
        assertThat(response.getCompletedCount()).isEqualTo(12);
        assertThat(response.getStatus()).isEqualTo(PushupStatus.DONE);
        assertThat(response.getCompletedDays()).isEqualTo(3);
        assertThat(response.getTotalDays()).isEqualTo(4);
        assertThat(response.getCurrentStreak()).isEqualTo(3);
        assertThat(response.getLongestStreak()).isEqualTo(3);
    }

    @Test
    void maintenanceStatsCalculateTotalsAndStreaks() {
        User user = new User();
        LocalDate today = LocalDate.now();
        List<MaintenanceEntry> entriesNewestFirst = List.of(
            maintenanceEntry(user, today, 35, 20),
            maintenanceEntry(user, today.minusDays(1), 30, 19),
            maintenanceEntry(user, today.minusDays(2), 25, 18),
            maintenanceEntry(user, today.minusDays(4), 20, 17)
        );

        when(maintenanceRepository.findByUserOrderByEntryDateDesc(user)).thenReturn(entriesNewestFirst);
        when(maintenanceRepository.findFirstByUserOrderByEntryDateDesc(user)).thenReturn(Optional.of(entriesNewestFirst.get(0)));

        PushupService service = new PushupService(pushupRepository, maintenanceRepository);
        PushupMaintenanceResponse response = service.getMaintenance(user);

        assertThat(response.getTotalDays()).isEqualTo(4);
        assertThat(response.getTotalPushups()).isEqualTo(110);
        assertThat(response.getAveragePerDay()).isEqualTo(27.5);
        assertThat(response.getCurrentStreak()).isEqualTo(3);
        assertThat(response.getLongestStreak()).isEqualTo(3);
    }

    private PushupChallengeRequest challengeRequest(LocalDate date, int completedCount, int challengeDay, Integer targetCount) {
        PushupChallengeRequest request = new PushupChallengeRequest();
        request.setEntryDate(date);
        request.setCompletedCount(completedCount);
        request.setChallengeDay(challengeDay);
        request.setTargetCount(targetCount);
        return request;
    }

    private PushupEntry challengeEntry(User user, LocalDate date, int challengeDay, int targetCount, int completedCount, PushupStatus status) {
        PushupEntry entry = new PushupEntry();
        entry.setUser(user);
        entry.setEntryDate(date);
        entry.setChallengeDay(challengeDay);
        entry.setTargetCount(targetCount);
        entry.setCompletedCount(completedCount);
        entry.setStatus(status);
        return entry;
    }

    private MaintenanceEntry maintenanceEntry(User user, LocalDate date, int pushups, Integer challengeDay) {
        MaintenanceEntry entry = new MaintenanceEntry();
        entry.setUser(user);
        entry.setEntryDate(date);
        entry.setPushupsCount(pushups);
        entry.setChallengeDay(challengeDay);
        return entry;
    }
}
