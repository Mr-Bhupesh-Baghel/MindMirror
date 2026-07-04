package com.mindmirror.backend.routine.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mindmirror.backend.exception.ApiException;
import com.mindmirror.backend.routine.dto.RoutineCompletionItemRequest;
import com.mindmirror.backend.routine.dto.RoutineCompletionItemResponse;
import com.mindmirror.backend.routine.dto.RoutineCompletionRequest;
import com.mindmirror.backend.routine.dto.RoutineCompletionResponse;
import com.mindmirror.backend.routine.dto.RoutineHistoryResponse;
import com.mindmirror.backend.routine.dto.RoutineTaskRequest;
import com.mindmirror.backend.routine.dto.RoutineTaskResponse;
import com.mindmirror.backend.routine.dto.UpdateRoutineTaskRequest;
import com.mindmirror.backend.routine.entity.RoutineCompletion;
import com.mindmirror.backend.routine.entity.RoutineTask;
import com.mindmirror.backend.routine.repository.RoutineCompletionRepository;
import com.mindmirror.backend.routine.repository.RoutineTaskRepository;
import com.mindmirror.backend.user.entity.User;

@Service
public class RoutineService {

    private static final List<DefaultTask> DEFAULT_TASKS = List.of(
        new DefaultTask("Select one task, breathe, focus, and keep doing", "daily", 1),
        new DefaultTask("Read one page", "daily", 2),
        new DefaultTask("Plan tomorrow", "daily", 3),
        new DefaultTask("Holiday reflection task", "holiday", 1)
    );

    private final RoutineTaskRepository taskRepository;
    private final RoutineCompletionRepository completionRepository;

    public RoutineService(RoutineTaskRepository taskRepository, RoutineCompletionRepository completionRepository) {
        this.taskRepository = taskRepository;
        this.completionRepository = completionRepository;
    }

    @Transactional
    public List<RoutineTaskResponse> tasks(User user) {
        ensureDefaultTasks(user);
        return activeTasks(user).stream()
            .map(this::toTaskResponse)
            .toList();
    }

    @Transactional
    public RoutineTaskResponse createTask(User user, RoutineTaskRequest request) {
        ensureDefaultTasks(user);
        String title = normalizeTitle(request.getTitle());
        String category = normalizeCategory(request.getCategory(), "custom");

        RoutineTask task = taskRepository.findByUserAndTitleAndCategory(user, title, category)
            .orElseGet(RoutineTask::new);
        task.setUser(user);
        task.setTitle(title);
        task.setCategory(category);
        task.setSortOrder(request.getSortOrder() == null ? nextSortOrder(user, category) : request.getSortOrder());
        task.setActive(true);

        return toTaskResponse(taskRepository.save(task));
    }

    @Transactional
    public RoutineTaskResponse updateTask(User user, Long id, UpdateRoutineTaskRequest request) {
        RoutineTask task = findTask(user, id);

        if (request.getTitle() != null) {
            task.setTitle(normalizeTitle(request.getTitle()));
        }
        if (request.getCategory() != null) {
            task.setCategory(normalizeCategory(request.getCategory(), task.getCategory()));
        }
        if (request.getSortOrder() != null) {
            task.setSortOrder(request.getSortOrder());
        }
        if (request.getActive() != null) {
            task.setActive(request.getActive());
        }

        return toTaskResponse(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(User user, Long id) {
        RoutineTask task = findTask(user, id);
        task.setActive(false);
        taskRepository.save(task);
    }

    @Transactional
    public RoutineCompletionResponse completions(User user, LocalDate date) {
        ensureDefaultTasks(user);
        return completionResponse(user, date == null ? LocalDate.now() : date);
    }

    @Transactional
    public RoutineCompletionResponse saveCompletions(User user, RoutineCompletionRequest request) {
        ensureDefaultTasks(user);
        LocalDate completionDate = request.getCompletionDate();

        for (RoutineCompletionItemRequest item : request.getCompletions()) {
            RoutineTask task = findTask(user, item.getTaskId());
            RoutineCompletion completion = completionRepository
                .findByUserAndTaskAndCompletionDate(user, task, completionDate)
                .orElseGet(() -> newCompletion(user, task, completionDate));
            completion.setCompleted(item.getCompleted());
            completionRepository.save(completion);
        }

        return completionResponse(user, completionDate);
    }

    @Transactional(readOnly = true)
    public List<RoutineHistoryResponse> history(User user, LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "from must be on or before to");
        }

        LocalDate start = from == null ? LocalDate.of(1000, 1, 1) : from;
        LocalDate end = to == null ? LocalDate.of(9999, 12, 31) : to;
        List<RoutineCompletion> completions = completionRepository
            .findByUserAndCompletionDateBetweenOrderByCompletionDateDesc(user, start, end);

        Map<LocalDate, List<RoutineCompletion>> byDate = completions.stream()
            .collect(Collectors.groupingBy(
                RoutineCompletion::getCompletionDate,
                LinkedHashMap::new,
                Collectors.toList()
            ));

        return byDate.entrySet().stream()
            .map(entry -> historyResponse(entry.getKey(), entry.getValue()))
            .sorted(Comparator.comparing(RoutineHistoryResponse::getCompletionDate).reversed())
            .toList();
    }

    @Transactional(readOnly = true)
    public String historyCsv(User user, LocalDate from, LocalDate to) {
        StringBuilder csv = new StringBuilder("Date,Completed Tasks,Total Tasks,Completion Rate\n");
        for (RoutineHistoryResponse row : history(user, from, to)) {
            csv.append(row.getCompletionDate())
                .append(',')
                .append(row.getCompletedTasks())
                .append(',')
                .append(row.getTotalTasks())
                .append(',')
                .append(row.getCompletionRate())
                .append('\n');
        }
        return csv.toString();
    }

    private RoutineCompletionResponse completionResponse(User user, LocalDate completionDate) {
        List<RoutineTask> tasks = activeTasks(user);
        Map<Long, RoutineCompletion> completions = completionRepository.findByUserAndCompletionDate(user, completionDate)
            .stream()
            .collect(Collectors.toMap(completion -> completion.getTask().getId(), Function.identity()));

        List<RoutineCompletionItemResponse> taskResponses = tasks.stream()
            .map(task -> toCompletionItemResponse(task, completions.get(task.getId())))
            .toList();

        int completedTasks = (int) taskResponses.stream()
            .filter(RoutineCompletionItemResponse::isCompleted)
            .count();

        RoutineCompletionResponse response = new RoutineCompletionResponse();
        response.setCompletionDate(completionDate);
        response.setTotalTasks(taskResponses.size());
        response.setCompletedTasks(completedTasks);
        response.setCompletionRate(percentage(completedTasks, taskResponses.size()));
        response.setTasks(taskResponses);
        return response;
    }

    private RoutineHistoryResponse historyResponse(LocalDate completionDate, List<RoutineCompletion> completions) {
        int totalTasks = completions.size();
        int completedTasks = (int) completions.stream()
            .filter(RoutineCompletion::isCompleted)
            .count();

        RoutineHistoryResponse response = new RoutineHistoryResponse();
        response.setCompletionDate(completionDate);
        response.setTotalTasks(totalTasks);
        response.setCompletedTasks(completedTasks);
        response.setCompletionRate(percentage(completedTasks, totalTasks));
        return response;
    }

    private RoutineCompletion newCompletion(User user, RoutineTask task, LocalDate completionDate) {
        RoutineCompletion completion = new RoutineCompletion();
        completion.setUser(user);
        completion.setTask(task);
        completion.setCompletionDate(completionDate);
        return completion;
    }

    private RoutineTask findTask(User user, Long id) {
        return taskRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Routine task not found"));
    }

    private List<RoutineTask> activeTasks(User user) {
        return taskRepository.findByUserAndActiveTrueOrderByCategoryAscSortOrderAscIdAsc(user);
    }

    private void ensureDefaultTasks(User user) {
        for (DefaultTask defaultTask : DEFAULT_TASKS) {
            if (taskRepository.findByUserAndTitleAndCategory(user, defaultTask.title(), defaultTask.category()).isEmpty()) {
                RoutineTask task = new RoutineTask();
                task.setUser(user);
                task.setTitle(defaultTask.title());
                task.setCategory(defaultTask.category());
                task.setSortOrder(defaultTask.sortOrder());
                task.setActive(true);
                taskRepository.save(task);
            }
        }
    }

    private int nextSortOrder(User user, String category) {
        return activeTasks(user).stream()
            .filter(task -> task.getCategory().equals(category))
            .map(RoutineTask::getSortOrder)
            .max(Integer::compareTo)
            .orElse(0) + 1;
    }

    private String normalizeTitle(String title) {
        String normalized = title == null ? "" : title.trim();
        if (normalized.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "title is required");
        }
        return normalized;
    }

    private String normalizeCategory(String category, String defaultCategory) {
        String normalized = category == null || category.isBlank() ? defaultCategory : category.trim().toLowerCase();
        if (normalized.length() > 80) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "category must be 80 characters or fewer");
        }
        return normalized;
    }

    private RoutineTaskResponse toTaskResponse(RoutineTask task) {
        RoutineTaskResponse response = new RoutineTaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setCategory(task.getCategory());
        response.setSortOrder(task.getSortOrder());
        response.setActive(task.isActive());
        return response;
    }

    private RoutineCompletionItemResponse toCompletionItemResponse(RoutineTask task, RoutineCompletion completion) {
        RoutineCompletionItemResponse response = new RoutineCompletionItemResponse();
        response.setTaskId(task.getId());
        response.setTitle(task.getTitle());
        response.setCategory(task.getCategory());
        response.setSortOrder(task.getSortOrder());
        response.setCompleted(completion != null && completion.isCompleted());
        return response;
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

    private record DefaultTask(String title, String category, int sortOrder) {
    }
}
