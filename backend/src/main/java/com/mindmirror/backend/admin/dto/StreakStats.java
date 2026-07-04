package com.mindmirror.backend.admin.dto;

public record StreakStats(
    int averageCurrentStreak,
    int longestCurrentStreak,
    int longestStreak
) {
}
