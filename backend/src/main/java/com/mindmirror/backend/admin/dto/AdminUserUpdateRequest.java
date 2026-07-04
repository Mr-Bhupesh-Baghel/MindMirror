package com.mindmirror.backend.admin.dto;

import com.mindmirror.backend.user.entity.UserRole;
import com.mindmirror.backend.user.entity.UserStatus;

import jakarta.validation.constraints.Size;

public record AdminUserUpdateRequest(
    @Size(max = 120)
    String displayName,
    UserRole role,
    UserStatus status
) {
}
