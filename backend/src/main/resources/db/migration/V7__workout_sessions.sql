CREATE TABLE workout_sessions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    exercise_name VARCHAR(120) NOT NULL,
    starting_number INT NOT NULL,
    total_reps INT NOT NULL,
    completed_on DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_workout_sessions_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT chk_workout_sessions_values CHECK (starting_number > 0 AND total_reps > 0)
);

CREATE INDEX idx_workout_sessions_user_date ON workout_sessions (user_id, completed_on);
