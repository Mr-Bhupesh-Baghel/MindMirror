CREATE TABLE user_sync_status (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    migration_state VARCHAR(40) NOT NULL DEFAULT 'NOT_STARTED',
    sync_state VARCHAR(40) NOT NULL DEFAULT 'IDLE',
    uploaded_count INT NOT NULL DEFAULT 0,
    failed_count INT NOT NULL DEFAULT 0,
    queued_count INT NOT NULL DEFAULT 0,
    conflict_count INT NOT NULL DEFAULT 0,
    last_error VARCHAR(1000) NULL,
    last_migration_at TIMESTAMP NULL,
    last_sync_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_user_sync_status_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE,
    CONSTRAINT uq_user_sync_status_user UNIQUE (user_id)
);

CREATE INDEX idx_user_sync_status_updated_at ON user_sync_status (updated_at);
