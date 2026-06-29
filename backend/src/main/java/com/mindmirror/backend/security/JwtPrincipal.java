package com.mindmirror.backend.security;

public record JwtPrincipal(
    Long userId,
    String email,
    String role
) {
}
