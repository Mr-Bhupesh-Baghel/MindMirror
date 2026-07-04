package com.mindmirror.backend.sync;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mindmirror.backend.security.AuthenticatedUser;
import com.mindmirror.backend.sync.dto.SyncReportRequest;
import com.mindmirror.backend.sync.dto.SyncStatusResponse;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/sync")
public class SyncController {

    private final SyncStatusService syncStatusService;

    public SyncController(SyncStatusService syncStatusService) {
        this.syncStatusService = syncStatusService;
    }

    @PostMapping
    SyncStatusResponse sync(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @Valid @RequestBody(required = false) SyncReportRequest request
    ) {
        return syncStatusService.recordSync(authenticatedUser.getUser(), request);
    }

    @GetMapping("/status")
    SyncStatusResponse status(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return syncStatusService.status(authenticatedUser.getUser());
    }
}
