package com.mindmirror.backend.sync;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mindmirror.backend.security.AuthenticatedUser;
import com.mindmirror.backend.sync.dto.SyncStatusResponse;

@RestController
@RequestMapping("/api/migration")
public class MigrationController {

    private final SyncStatusService syncStatusService;

    public MigrationController(SyncStatusService syncStatusService) {
        this.syncStatusService = syncStatusService;
    }

    @PostMapping("/start")
    SyncStatusResponse start(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return syncStatusService.startMigration(authenticatedUser.getUser());
    }

    @GetMapping("/status")
    SyncStatusResponse status(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return syncStatusService.status(authenticatedUser.getUser());
    }
}
