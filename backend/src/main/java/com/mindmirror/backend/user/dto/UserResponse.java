package com.mindmirror.backend.user.dto;

import java.time.Instant;

import com.mindmirror.backend.user.entity.User;
import com.mindmirror.backend.user.entity.UserRole;

public record UserResponse(
    Long id,
    String email,
    String displayName,
    UserRole role,
    Instant createdAt,
    Instant updatedAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getDisplayName(),
            user.getRole(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
