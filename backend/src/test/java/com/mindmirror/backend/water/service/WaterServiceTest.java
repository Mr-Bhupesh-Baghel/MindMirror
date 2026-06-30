package com.mindmirror.backend.water.service;

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

import com.mindmirror.backend.user.entity.User;
import com.mindmirror.backend.water.dto.WaterRequest;
import com.mindmirror.backend.water.dto.WaterResponse;
import com.mindmirror.backend.water.dto.WaterStatsResponse;
import com.mindmirror.backend.water.entity.WaterEntry;
import com.mindmirror.backend.water.repository.WaterRepository;

@ExtendWith(MockitoExtension.class)
class WaterServiceTest {

    @Mock
    private WaterRepository waterRepository;

    @Test
    void saveUpdatesExistingDailyEntry() {
        User user = new User();
        LocalDate date = LocalDate.of(2026, 6, 30);
        WaterEntry existingEntry = entry(user, date, 4, 8);
        WaterRequest request = request(date, 9, null);

        when(waterRepository.findByUserAndEntryDate(user, date)).thenReturn(Optional.of(existingEntry));
        when(waterRepository.save(any(WaterEntry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WaterService service = new WaterService(waterRepository);
        WaterResponse response = service.save(user, request);

        ArgumentCaptor<WaterEntry> captor = ArgumentCaptor.forClass(WaterEntry.class);
        verify(waterRepository).save(captor.capture());

        assertThat(captor.getValue()).isSameAs(existingEntry);
        assertThat(response.getGlasses()).isEqualTo(9);
        assertThat(response.getGoalGlasses()).isEqualTo(8);
        assertThat(response.isGoalMet()).isTrue();
    }

    @Test
    void statsCalculatesTotalsAndGoalStreaks() {
        User user = new User();
        LocalDate today = LocalDate.now();
        List<WaterEntry> entriesNewestFirst = List.of(
            entry(user, today, 8, 8),
            entry(user, today.minusDays(1), 9, 8),
            entry(user, today.minusDays(2), 7, 8),
            entry(user, today.minusDays(3), 8, 8),
            entry(user, today.minusDays(4), 8, 8)
        );

        when(waterRepository.findByUserOrderByEntryDateDesc(user)).thenReturn(entriesNewestFirst);

        WaterService service = new WaterService(waterRepository);
        WaterStatsResponse response = service.stats(user);

        assertThat(response.getTotalDays()).isEqualTo(5);
        assertThat(response.getGoalDays()).isEqualTo(4);
        assertThat(response.getTotalGlasses()).isEqualTo(40);
        assertThat(response.getCurrentStreak()).isEqualTo(2);
        assertThat(response.getLongestStreak()).isEqualTo(2);
        assertThat(response.getAveragePerDay()).isEqualTo(8.0);
    }

    private WaterRequest request(LocalDate date, int glasses, Integer goalGlasses) {
        WaterRequest request = new WaterRequest();
        request.setEntryDate(date);
        request.setGlasses(glasses);
        request.setGoalGlasses(goalGlasses);
        return request;
    }

    private WaterEntry entry(User user, LocalDate date, int glasses, int goalGlasses) {
        WaterEntry entry = new WaterEntry();
        entry.setUser(user);
        entry.setEntryDate(date);
        entry.setGlassesCount(glasses);
        entry.setGoalGlasses(goalGlasses);
        return entry;
    }
}
