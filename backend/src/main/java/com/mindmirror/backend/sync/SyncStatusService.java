package com.mindmirror.backend.sync;

import java.time.Instant;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mindmirror.backend.sync.dto.SyncReportRequest;
import com.mindmirror.backend.sync.dto.SyncStatusResponse;
import com.mindmirror.backend.sync.entity.UserSyncStatus;
import com.mindmirror.backend.sync.repository.UserSyncStatusRepository;
import com.mindmirror.backend.user.entity.User;

@Service
public class SyncStatusService {

    private final UserSyncStatusRepository repository;

    public SyncStatusService(UserSyncStatusRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public SyncStatusResponse startMigration(User user) {
        UserSyncStatus status = findOrCreate(user);
        status.setMigrationState("RUNNING");
        status.setSyncState("RUNNING");
        status.setFailedCount(0);
        status.setQueuedCount(0);
        status.setLastError(null);
        status.setLastMigrationAt(Instant.now());
        status.setLastSyncAt(Instant.now());
        return SyncStatusResponse.from(repository.save(status));
    }

    @Transactional
    public SyncStatusResponse recordSync(User user, SyncReportRequest request) {
        UserSyncStatus status = findOrCreate(user);
        String state = normalizeState(request == null ? null : request.state());
        status.setSyncState(state);
        status.setUploadedCount(valueOrCurrent(request == null ? null : request.uploaded(), status.getUploadedCount()));
        status.setFailedCount(valueOrCurrent(request == null ? null : request.failed(), status.getFailedCount()));
        status.setQueuedCount(valueOrCurrent(request == null ? null : request.queued(), status.getQueuedCount()));
        status.setConflictCount(valueOrCurrent(request == null ? null : request.conflicts(), status.getConflictCount()));
        status.setLastError(trimToNull(request == null ? null : request.lastError()));
        status.setLastSyncAt(Instant.now());

        if ("COMPLETE".equals(state)) {
            status.setMigrationState("COMPLETE");
            status.setLastMigrationAt(status.getLastSyncAt());
        } else if ("PARTIAL".equals(state) || "FAILED".equals(state) || "OFFLINE".equals(state)) {
            status.setMigrationState(state);
        }

        return SyncStatusResponse.from(repository.save(status));
    }

    @Transactional(readOnly = true)
    public SyncStatusResponse status(User user) {
        return SyncStatusResponse.from(repository.findByUser(user).orElseGet(() -> defaultStatus(user)));
    }

    private UserSyncStatus findOrCreate(User user) {
        return repository.findByUser(user).orElseGet(() -> defaultStatus(user));
    }

    private UserSyncStatus defaultStatus(User user) {
        UserSyncStatus status = new UserSyncStatus();
        status.setUser(user);
        return status;
    }

    private String normalizeState(String state) {
        if (state == null || state.isBlank()) {
            return "COMPLETE";
        }
        String normalized = state.trim().toUpperCase(Locale.ROOT).replace('-', '_');
        return switch (normalized) {
            case "RUNNING", "COMPLETE", "PARTIAL", "FAILED", "OFFLINE", "IDLE" -> normalized;
            default -> "COMPLETE";
        };
    }

    private int valueOrCurrent(Integer value, Integer current) {
        return value == null ? current : Math.max(0, value);
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
