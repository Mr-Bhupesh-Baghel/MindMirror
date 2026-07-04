package com.mindmirror.backend.pushups.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.mindmirror.backend.pushups.dto.PushupChallengeHistoryResponse;
import com.mindmirror.backend.pushups.dto.PushupChallengeRequest;
import com.mindmirror.backend.pushups.dto.PushupChallengeResponse;
import com.mindmirror.backend.pushups.dto.PushupMaintenanceHistoryResponse;
import com.mindmirror.backend.pushups.dto.PushupMaintenanceRequest;
import com.mindmirror.backend.pushups.dto.PushupMaintenanceResponse;
import com.mindmirror.backend.pushups.service.PushupService;
import com.mindmirror.backend.security.AuthenticatedUser;
import com.mindmirror.backend.user.entity.User;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/pushups")
public class PushupController {

    private final PushupService pushupService;

    public PushupController(PushupService pushupService) {
        this.pushupService = pushupService;
    }

    @GetMapping("/challenge")
    PushupChallengeResponse challenge(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return pushupService.getChallenge(currentUser(authenticatedUser));
    }

    @PutMapping("/challenge")
    @ResponseStatus(HttpStatus.OK)
    PushupChallengeResponse saveChallenge(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @Valid @RequestBody PushupChallengeRequest request
    ) {
        return pushupService.saveChallenge(currentUser(authenticatedUser), request);
    }

    @GetMapping("/challenge/history")
    List<PushupChallengeHistoryResponse> challengeHistory(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return pushupService.challengeHistory(currentUser(authenticatedUser));
    }

    @GetMapping("/maintenance")
    PushupMaintenanceResponse maintenance(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return pushupService.getMaintenance(currentUser(authenticatedUser));
    }

    @PutMapping("/maintenance")
    @ResponseStatus(HttpStatus.OK)
    PushupMaintenanceResponse saveMaintenance(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @Valid @RequestBody PushupMaintenanceRequest request
    ) {
        return pushupService.saveMaintenance(currentUser(authenticatedUser), request);
    }

    @GetMapping("/maintenance/history")
    List<PushupMaintenanceHistoryResponse> maintenanceHistory(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return pushupService.maintenanceHistory(currentUser(authenticatedUser));
    }

    private User currentUser(AuthenticatedUser authenticatedUser) {
        if (authenticatedUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        return authenticatedUser.getUser();
    }
}
