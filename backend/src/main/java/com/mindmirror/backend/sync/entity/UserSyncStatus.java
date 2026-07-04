package com.mindmirror.backend.sync.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.mindmirror.backend.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "user_sync_status",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "user_id")
    }
)
public class UserSyncStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "migration_state", nullable = false)
    private String migrationState = "NOT_STARTED";

    @Column(name = "sync_state", nullable = false)
    private String syncState = "IDLE";

    @Column(name = "uploaded_count", nullable = false)
    private Integer uploadedCount = 0;

    @Column(name = "failed_count", nullable = false)
    private Integer failedCount = 0;

    @Column(name = "queued_count", nullable = false)
    private Integer queuedCount = 0;

    @Column(name = "conflict_count", nullable = false)
    private Integer conflictCount = 0;

    @Column(name = "last_error", length = 1000)
    private String lastError;

    @Column(name = "last_migration_at")
    private Instant lastMigrationAt;

    @Column(name = "last_sync_at")
    private Instant lastSyncAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMigrationState() {
        return migrationState;
    }

    public void setMigrationState(String migrationState) {
        this.migrationState = migrationState;
    }

    public String getSyncState() {
        return syncState;
    }

    public void setSyncState(String syncState) {
        this.syncState = syncState;
    }

    public Integer getUploadedCount() {
        return uploadedCount;
    }

    public void setUploadedCount(Integer uploadedCount) {
        this.uploadedCount = uploadedCount;
    }

    public Integer getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(Integer failedCount) {
        this.failedCount = failedCount;
    }

    public Integer getQueuedCount() {
        return queuedCount;
    }

    public void setQueuedCount(Integer queuedCount) {
        this.queuedCount = queuedCount;
    }

    public Integer getConflictCount() {
        return conflictCount;
    }

    public void setConflictCount(Integer conflictCount) {
        this.conflictCount = conflictCount;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public Instant getLastMigrationAt() {
        return lastMigrationAt;
    }

    public void setLastMigrationAt(Instant lastMigrationAt) {
        this.lastMigrationAt = lastMigrationAt;
    }

    public Instant getLastSyncAt() {
        return lastSyncAt;
    }

    public void setLastSyncAt(Instant lastSyncAt) {
        this.lastSyncAt = lastSyncAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
