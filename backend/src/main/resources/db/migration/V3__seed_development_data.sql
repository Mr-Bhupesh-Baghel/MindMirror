INSERT INTO users (email, password_hash, display_name, role, status)
VALUES
    ('demo@mindmirror.local', '$2a$10$development.only.password.hash', 'Demo User', 'USER', 'ACTIVE');

SET @demo_user_id = LAST_INSERT_ID();

INSERT INTO routine_tasks (user_id, title, category, sort_order)
VALUES
    (@demo_user_id, 'Select one task, breathe, focus, and keep doing', 'daily', 1),
    (@demo_user_id, 'Read one page', 'daily', 2),
    (@demo_user_id, 'Plan tomorrow', 'daily', 3),
    (@demo_user_id, 'Holiday reflection task', 'holiday', 1);

INSERT INTO routine_completions (user_id, routine_task_id, completion_date, is_completed)
SELECT @demo_user_id, id, CURRENT_DATE, TRUE
FROM routine_tasks
WHERE user_id = @demo_user_id
  AND category = 'daily'
  AND sort_order <= 2;

INSERT INTO water_entries (user_id, entry_date, glasses_count, goal_glasses)
VALUES
    (@demo_user_id, CURRENT_DATE, 4, 8);

INSERT INTO pushup_entries (user_id, challenge_day, entry_date, target_count, completed_count, status)
VALUES
    (@demo_user_id, 1, CURRENT_DATE, 1, 10, 'DONE');

INSERT INTO maintenance_entries (user_id, entry_date, pushups_count, challenge_day)
VALUES
    (@demo_user_id, CURRENT_DATE, 25, 1);

INSERT INTO feedback_entries (user_id, name, email, rating, message, feedback_date)
VALUES
    (@demo_user_id, 'Demo User', 'demo@mindmirror.local', 5, 'Development seed feedback.', CURRENT_DATE);

INSERT INTO affirmations (user_id, text)
VALUES
    (@demo_user_id, 'I can focus on one meaningful task at a time.'),
    (@demo_user_id, 'Small daily actions build discipline.');
