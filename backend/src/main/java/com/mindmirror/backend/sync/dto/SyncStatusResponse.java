package com.mindmirror.backend.sync.dto;

import java.time.Instant;

import com.mindmirror.backend.sync.entity.UserSyncStatus;

public record SyncStatusResponse(
    String migrationState,
    String syncState,
    int uploaded,
    int failed,
    int queued,
    int conflicts,
    String lastError,
    Instant lastMigrationAt,
    Instant lastSyncAt,
    Instant updatedAt
) {

    public static SyncStatusResponse from(UserSyncStatus status) {
        return new SyncStatusResponse(
            status.getMigrationState(),
            status.getSyncState(),
            status.getUploadedCount(),
            status.getFailedCount(),
            status.getQueuedCount(),
            status.getConflictCount(),
            status.getLastError(),
            status.getLastMigrationAt(),
            status.getLastSyncAt(),
            status.getUpdatedAt()
        );
    }
}
