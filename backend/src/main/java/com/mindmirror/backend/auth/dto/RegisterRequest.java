package com.mindmirror.backend.auth.dto;

import com.mindmirror.backend.validation.ValidPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    String email,

    @NotBlank(message = "Password is required")
    @ValidPassword
    String password,

    @NotBlank(message = "Display name is required")
    @Size(max = 120, message = "Display name must not exceed 120 characters")
    String displayName
) {
}
