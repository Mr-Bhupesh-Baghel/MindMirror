package com.mindmirror.backend.auth.dto;

import com.mindmirror.backend.user.dto.UserResponse;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresInSeconds,
    UserResponse user
) {
}
