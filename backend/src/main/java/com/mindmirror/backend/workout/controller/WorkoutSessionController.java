package com.mindmirror.backend.workout.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.mindmirror.backend.security.AuthenticatedUser;
import com.mindmirror.backend.user.entity.User;
import com.mindmirror.backend.workout.dto.WorkoutSessionRequest;
import com.mindmirror.backend.workout.dto.WorkoutSessionResponse;
import com.mindmirror.backend.workout.service.WorkoutSessionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutSessionController {
    private final WorkoutSessionService service;
    public WorkoutSessionController(WorkoutSessionService service) { this.service = service; }
    @GetMapping public List<WorkoutSessionResponse> history(@AuthenticationPrincipal AuthenticatedUser user) { return service.history(currentUser(user)); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public WorkoutSessionResponse create(@AuthenticationPrincipal AuthenticatedUser user, @Valid @RequestBody WorkoutSessionRequest request) { return service.create(currentUser(user), request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long id) { service.delete(currentUser(user), id); }
    private User currentUser(AuthenticatedUser user) { if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required"); return user.getUser(); }
}
