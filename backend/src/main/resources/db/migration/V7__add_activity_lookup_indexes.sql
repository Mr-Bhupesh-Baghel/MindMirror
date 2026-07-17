-- Composite indexes match the user-and-date access pattern used by history,
-- streak, and account-summary queries.
CREATE INDEX idx_water_entries_user_entry_date ON water_entries (user_id, entry_date);
CREATE INDEX idx_pushup_entries_user_entry_date ON pushup_entries (user_id, entry_date);
CREATE INDEX idx_maintenance_entries_user_entry_date ON maintenance_entries (user_id, entry_date);
CREATE INDEX idx_routine_completions_user_completion_date ON routine_completions (user_id, completion_date);
