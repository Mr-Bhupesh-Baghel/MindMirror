package com.mindmirror.backend.health;

import java.time.Instant;

public record HealthResponse(String status, boolean database, Instant timestamp) {
}
