package com.mindmirror.backend.admin.dto;

import java.time.Instant;

public record AdminStatsResponse(
    long totalUsers,
    long activeUsers,
    long deletedUsers,
    long dailyActiveUsers,
    StreakStats waterStreaks,
    StreakStats pushupStreaks,
    RoutineCompletionStats routineCompletion,
    RetentionStats userRetention,
    Instant generatedAt
) {
}
