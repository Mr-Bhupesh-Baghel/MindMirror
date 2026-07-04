package com.mindmirror.backend.admin.dto;

public record RoutineCompletionStats(
    long totalEntries,
    long completedEntries,
    double completionRate
) {
}
