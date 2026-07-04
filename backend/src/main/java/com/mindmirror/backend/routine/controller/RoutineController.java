package com.mindmirror.backend.routine.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.mindmirror.backend.routine.dto.RoutineCompletionRequest;
import com.mindmirror.backend.routine.dto.RoutineCompletionResponse;
import com.mindmirror.backend.routine.dto.RoutineHistoryResponse;
import com.mindmirror.backend.routine.dto.RoutineTaskRequest;
import com.mindmirror.backend.routine.dto.RoutineTaskResponse;
import com.mindmirror.backend.routine.dto.UpdateRoutineTaskRequest;
import com.mindmirror.backend.routine.service.RoutineService;
import com.mindmirror.backend.security.AuthenticatedUser;
import com.mindmirror.backend.user.entity.User;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/routine")
public class RoutineController {

    private final RoutineService routineService;

    public RoutineController(RoutineService routineService) {
        this.routineService = routineService;
    }

    @GetMapping("/tasks")
    List<RoutineTaskResponse> tasks(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return routineService.tasks(currentUser(authenticatedUser));
    }

    @PostMapping("/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    RoutineTaskResponse createTask(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @Valid @RequestBody RoutineTaskRequest request
    ) {
        return routineService.createTask(currentUser(authenticatedUser), request);
    }

    @PatchMapping("/tasks/{id}")
    RoutineTaskResponse updateTask(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @PathVariable Long id,
        @Valid @RequestBody UpdateRoutineTaskRequest request
    ) {
        return routineService.updateTask(currentUser(authenticatedUser), id, request);
    }

    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteTask(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @PathVariable Long id
    ) {
        routineService.deleteTask(currentUser(authenticatedUser), id);
    }

    @GetMapping("/completions")
    RoutineCompletionResponse completions(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return routineService.completions(currentUser(authenticatedUser), date);
    }

    @PutMapping("/completions")
    RoutineCompletionResponse saveCompletions(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @Valid @RequestBody RoutineCompletionRequest request
    ) {
        return routineService.saveCompletions(currentUser(authenticatedUser), request);
    }

    @GetMapping("/history")
    List<RoutineHistoryResponse> history(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return routineService.history(currentUser(authenticatedUser), from, to);
    }

    @GetMapping("/history/export")
    ResponseEntity<String> historyExport(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        String csv = routineService.historyCsv(currentUser(authenticatedUser), from, to);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"routine-history.csv\"")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(csv);
    }

    private User currentUser(AuthenticatedUser authenticatedUser) {
        if (authenticatedUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        return authenticatedUser.getUser();
    }
}
