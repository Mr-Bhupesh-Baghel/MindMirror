package com.mindmirror.backend.affirmation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.mindmirror.backend.affirmation.dto.AffirmationRequest;
import com.mindmirror.backend.affirmation.dto.AffirmationResponse;
import com.mindmirror.backend.affirmation.service.AffirmationService;
import com.mindmirror.backend.security.AuthenticatedUser;
import com.mindmirror.backend.user.entity.User;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/affirmations")
public class AffirmationController {

    private final AffirmationService affirmationService;

    public AffirmationController(AffirmationService affirmationService) {
        this.affirmationService = affirmationService;
    }

    @GetMapping
    List<AffirmationResponse> affirmations(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return affirmationService.affirmations(currentUser(authenticatedUser));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    AffirmationResponse create(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @Valid @RequestBody AffirmationRequest request
    ) {
        return affirmationService.create(currentUser(authenticatedUser), request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @PathVariable Long id
    ) {
        affirmationService.delete(currentUser(authenticatedUser), id);
    }

    private User currentUser(AuthenticatedUser authenticatedUser) {
        if (authenticatedUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        return authenticatedUser.getUser();
    }
}
