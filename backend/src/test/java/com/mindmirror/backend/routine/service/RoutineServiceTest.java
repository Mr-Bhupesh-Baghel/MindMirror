package com.mindmirror.backend.routine.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.mindmirror.backend.routine.dto.RoutineCompletionItemRequest;
import com.mindmirror.backend.routine.dto.RoutineCompletionRequest;
import com.mindmirror.backend.routine.dto.RoutineCompletionResponse;
import com.mindmirror.backend.routine.dto.RoutineHistoryResponse;
import com.mindmirror.backend.routine.entity.RoutineCompletion;
import com.mindmirror.backend.routine.entity.RoutineTask;
import com.mindmirror.backend.routine.repository.RoutineCompletionRepository;
import com.mindmirror.backend.routine.repository.RoutineTaskRepository;
import com.mindmirror.backend.user.entity.User;

@ExtendWith(MockitoExtension.class)
class RoutineServiceTest {

    @Mock
    private RoutineTaskRepository taskRepository;

    @Mock
    private RoutineCompletionRepository completionRepository;

    @Test
    void saveCompletionsUpsertsDailyProgressAndCalculatesStats() {
        User user = new User();
        LocalDate date = LocalDate.of(2026, 7, 4);
        RoutineTask defaultTask = task(99L, "Default", "daily", 1);
        RoutineTask focusTask = task(1L, "Focus", "daily", 1);
        RoutineTask holidayTask = task(2L, "Reflect", "holiday", 1);

        RoutineCompletion focusCompletion = completion(user, focusTask, date, true);
        RoutineCompletion holidayCompletion = completion(user, holidayTask, date, false);

        when(taskRepository.findByUserAndTitleAndCategory(eq(user), anyString(), anyString()))
            .thenReturn(Optional.of(defaultTask));
        when(taskRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(focusTask));
        when(taskRepository.findByIdAndUser(2L, user)).thenReturn(Optional.of(holidayTask));
        when(completionRepository.findByUserAndTaskAndCompletionDate(user, focusTask, date)).thenReturn(Optional.empty());
        when(completionRepository.findByUserAndTaskAndCompletionDate(user, holidayTask, date)).thenReturn(Optional.empty());
        when(completionRepository.findByUserAndCompletionDate(user, date))
            .thenReturn(List.of(focusCompletion, holidayCompletion));
        when(taskRepository.findByUserAndActiveTrueOrderByCategoryAscSortOrderAscIdAsc(user))
            .thenReturn(List.of(focusTask, holidayTask));
        when(completionRepository.save(any(RoutineCompletion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RoutineService service = new RoutineService(taskRepository, completionRepository);
        RoutineCompletionResponse response = service.saveCompletions(user, request(date, item(1L, true), item(2L, false)));

        assertThat(response.getCompletionDate()).isEqualTo(date);
        assertThat(response.getTotalTasks()).isEqualTo(2);
        assertThat(response.getCompletedTasks()).isEqualTo(1);
        assertThat(response.getCompletionRate()).isEqualTo(50.0);
        assertThat(response.getTasks()).extracting("taskId").containsExactly(1L, 2L);
    }

    @Test
    void historyReturnsExportFriendlyDateSummaries() {
        User user = new User();
        LocalDate date = LocalDate.of(2026, 7, 4);
        RoutineTask taskOne = task(1L, "Focus", "daily", 1);
        RoutineTask taskTwo = task(2L, "Reflect", "holiday", 1);

        when(completionRepository.findByUserAndCompletionDateBetweenOrderByCompletionDateDesc(user, date, date))
            .thenReturn(List.of(
                completion(user, taskOne, date, true),
                completion(user, taskTwo, date, false)
            ));

        RoutineService service = new RoutineService(taskRepository, completionRepository);
        List<RoutineHistoryResponse> history = service.history(user, date, date);

        assertThat(history).hasSize(1);
        assertThat(history.get(0).getCompletionDate()).isEqualTo(date);
        assertThat(history.get(0).getTotalTasks()).isEqualTo(2);
        assertThat(history.get(0).getCompletedTasks()).isEqualTo(1);
        assertThat(history.get(0).getCompletionRate()).isEqualTo(50.0);
    }

    private RoutineCompletionRequest request(LocalDate date, RoutineCompletionItemRequest... items) {
        RoutineCompletionRequest request = new RoutineCompletionRequest();
        request.setCompletionDate(date);
        request.setCompletions(List.of(items));
        return request;
    }

    private RoutineCompletionItemRequest item(Long taskId, boolean completed) {
        RoutineCompletionItemRequest item = new RoutineCompletionItemRequest();
        item.setTaskId(taskId);
        item.setCompleted(completed);
        return item;
    }

    private RoutineTask task(Long id, String title, String category, int sortOrder) {
        RoutineTask task = new RoutineTask();
        ReflectionTestUtils.setField(task, "id", id);
        task.setTitle(title);
        task.setCategory(category);
        task.setSortOrder(sortOrder);
        task.setActive(true);
        return task;
    }

    private RoutineCompletion completion(User user, RoutineTask task, LocalDate date, boolean completed) {
        RoutineCompletion completion = new RoutineCompletion();
        completion.setUser(user);
        completion.setTask(task);
        completion.setCompletionDate(date);
        completion.setCompleted(completed);
        return completion;
    }
}
