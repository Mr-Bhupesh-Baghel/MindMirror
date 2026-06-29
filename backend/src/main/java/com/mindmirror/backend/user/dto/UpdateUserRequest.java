package com.mindmirror.backend.user.dto;

import com.mindmirror.backend.validation.ValidPassword;

import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
    @Size(min = 1, max = 120, message = "Display name must be between 1 and 120 characters")
    String displayName,

    String currentPassword,

    @ValidPassword
    String newPassword
) {
}
