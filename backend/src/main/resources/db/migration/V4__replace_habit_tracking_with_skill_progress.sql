DROP TABLE IF EXISTS daily_habit_completion;
CREATE TABLE skill_progress (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    skill_key VARCHAR(32) NOT NULL,
    completed_lessons INTEGER NOT NULL DEFAULT 0,
    total_lessons INTEGER NOT NULL,
    last_completed_on DATE,
    CONSTRAINT skill_progress_unique UNIQUE (user_id, skill_key)
);
CREATE INDEX skill_progress_user_idx ON skill_progress(user_id);
