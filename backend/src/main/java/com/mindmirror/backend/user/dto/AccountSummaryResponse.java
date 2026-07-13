package com.mindmirror.backend.user.dto;

public record AccountSummaryResponse(
    long routineDays,
    int waterGlasses,
    int pushups,
    int currentStreak,
    boolean darkMode,
    boolean notifications
) {
}
