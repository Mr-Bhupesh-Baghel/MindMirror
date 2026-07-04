package com.mindmirror.backend.admin.dto;

public record RetentionStats(
    long eligibleUsers,
    long retainedUsers,
    double retentionRate
) {
}
