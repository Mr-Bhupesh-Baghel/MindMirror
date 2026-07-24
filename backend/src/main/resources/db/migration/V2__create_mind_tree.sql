CREATE TABLE daily_habit_completion (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    habit_key VARCHAR(32) NOT NULL,
    completed_on DATE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT daily_habit_completion_unique UNIQUE (user_id, habit_key, completed_on)
);
CREATE INDEX daily_habit_completion_user_date_idx ON daily_habit_completion(user_id, completed_on);
CREATE TABLE tree_achievement (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    achievement_key VARCHAR(32) NOT NULL,
    unlocked_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT tree_achievement_unique UNIQUE (user_id, achievement_key)
);
