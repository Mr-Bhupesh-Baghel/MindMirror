CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(120) NOT NULL,
    role VARCHAR(40) NOT NULL DEFAULT 'USER',
    status VARCHAR(40) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE routine_tasks (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(80) NOT NULL DEFAULT 'daily',
    sort_order INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_routine_tasks_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE,
    CONSTRAINT uq_routine_tasks_user_title_category UNIQUE (user_id, title, category)
);

CREATE TABLE routine_completions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    routine_task_id BIGINT NOT NULL,
    completion_date DATE NOT NULL,
    is_completed BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_routine_completions_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_routine_completions_task
        FOREIGN KEY (routine_task_id) REFERENCES routine_tasks (id)
        ON DELETE CASCADE,
    CONSTRAINT uq_routine_completions_task_date UNIQUE (routine_task_id, completion_date)
);

CREATE TABLE water_entries (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    entry_date DATE NOT NULL,
    glasses_count INT NOT NULL DEFAULT 0,
    goal_glasses INT NOT NULL DEFAULT 8,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_water_entries_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE,
    CONSTRAINT uq_water_entries_user_date UNIQUE (user_id, entry_date),
    CONSTRAINT chk_water_entries_counts CHECK (glasses_count >= 0 AND goal_glasses > 0)
);

CREATE TABLE pushup_entries (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    challenge_day INT NOT NULL,
    entry_date DATE NOT NULL,
    target_count INT NOT NULL,
    completed_count INT NOT NULL DEFAULT 0,
    status VARCHAR(40) NOT NULL DEFAULT 'DONE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_pushup_entries_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE,
    CONSTRAINT uq_pushup_entries_user_challenge_day UNIQUE (user_id, challenge_day),
    CONSTRAINT chk_pushup_entries_counts CHECK (
        challenge_day > 0
        AND target_count >= 0
        AND completed_count >= 0
    )
);

CREATE TABLE maintenance_entries (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    entry_date DATE NOT NULL,
    pushups_count INT NOT NULL,
    challenge_day INT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_maintenance_entries_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE,
    CONSTRAINT uq_maintenance_entries_user_date UNIQUE (user_id, entry_date),
    CONSTRAINT chk_maintenance_entries_counts CHECK (
        pushups_count > 0
        AND (challenge_day IS NULL OR challenge_day > 0)
    )
);

CREATE TABLE feedback_entries (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NULL,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(255) NOT NULL,
    rating TINYINT NOT NULL,
    message TEXT NOT NULL,
    feedback_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_feedback_entries_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE SET NULL,
    CONSTRAINT chk_feedback_entries_rating CHECK (rating BETWEEN 1 AND 5)
);

CREATE TABLE affirmations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    text VARCHAR(500) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_affirmations_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE,
    CONSTRAINT uq_affirmations_user_text UNIQUE (user_id, text)
);

CREATE INDEX idx_users_created_at ON users (created_at);

CREATE INDEX idx_routine_tasks_user_id ON routine_tasks (user_id);
CREATE INDEX idx_routine_tasks_created_at ON routine_tasks (created_at);

CREATE INDEX idx_routine_completions_user_id ON routine_completions (user_id);
CREATE INDEX idx_routine_completions_date ON routine_completions (completion_date);
CREATE INDEX idx_routine_completions_created_at ON routine_completions (created_at);

CREATE INDEX idx_water_entries_user_id ON water_entries (user_id);
CREATE INDEX idx_water_entries_date ON water_entries (entry_date);
CREATE INDEX idx_water_entries_created_at ON water_entries (created_at);

CREATE INDEX idx_pushup_entries_user_id ON pushup_entries (user_id);
CREATE INDEX idx_pushup_entries_date ON pushup_entries (entry_date);
CREATE INDEX idx_pushup_entries_created_at ON pushup_entries (created_at);

CREATE INDEX idx_maintenance_entries_user_id ON maintenance_entries (user_id);
CREATE INDEX idx_maintenance_entries_date ON maintenance_entries (entry_date);
CREATE INDEX idx_maintenance_entries_created_at ON maintenance_entries (created_at);

CREATE INDEX idx_feedback_entries_user_id ON feedback_entries (user_id);
CREATE INDEX idx_feedback_entries_email ON feedback_entries (email);
CREATE INDEX idx_feedback_entries_date ON feedback_entries (feedback_date);
CREATE INDEX idx_feedback_entries_created_at ON feedback_entries (created_at);

CREATE INDEX idx_affirmations_user_id ON affirmations (user_id);
CREATE INDEX idx_affirmations_created_at ON affirmations (created_at);
