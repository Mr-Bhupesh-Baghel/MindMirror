package com.mindmirror.backend.admin.dto;

import java.time.Instant;

import com.mindmirror.backend.user.entity.User;
import com.mindmirror.backend.user.entity.UserRole;
import com.mindmirror.backend.user.entity.UserStatus;

public record AdminUserResponse(
    Long id,
    String email,
    String displayName,
    UserRole role,
    UserStatus status,
    Instant createdAt,
    Instant updatedAt
) {
    public static AdminUserResponse from(User user) {
        return new AdminUserResponse(
            user.getId(),
            user.getEmail(),
            user.getDisplayName(),
            user.getRole(),
            user.getStatus(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
