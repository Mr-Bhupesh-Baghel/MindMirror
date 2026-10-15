package com.mindmirror.authservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
public class TokenService {
    private final String secret;
    private final long ttlSeconds;

    public TokenService(
            @Value("${mindmirror.token-secret}") String secret,
            @Value("${mindmirror.token-ttl-seconds}") long ttlSeconds) {
        this.secret = secret;
        this.ttlSeconds = ttlSeconds;
    }

    public String issue(AuthUser user) {
        long expiresAt = Instant.now().plusSeconds(ttlSeconds).getEpochSecond();
        String email = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(user.getEmail().getBytes(StandardCharsets.UTF_8));
        String displayName = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(user.getDisplayName().getBytes(StandardCharsets.UTF_8));
        String payload = user.getId() + "." + expiresAt + "." + email + "." + displayName;
        return payload + "." + sign(payload);
    }

    public AuthenticatedUser requireUser(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
        }

        String token = authorization.substring("Bearer ".length()).trim();
        String[] parts = token.split("\\.", -1);
        String payload = parts.length == 5 ? parts[0] + "." + parts[1] + "." + parts[2] + "." + parts[3] : "";
        if (parts.length != 5 || !constantTimeEquals(parts[4], sign(payload))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication token");
        }

        try {
            long expiresAt = Long.parseLong(parts[1]);
            if (expiresAt <= Instant.now().getEpochSecond()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication token has expired");
            }
            String email = new String(Base64.getUrlDecoder().decode(parts[2]), StandardCharsets.UTF_8);
            String displayName = new String(Base64.getUrlDecoder().decode(parts[3]), StandardCharsets.UTF_8);
            UUID.fromString(parts[0]);
            return new AuthenticatedUser(parts[0], email, displayName);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication token");
        }
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Could not sign authentication token", exception);
        }
    }

    private boolean constantTimeEquals(String first, String second) {
        return java.security.MessageDigest.isEqual(
                first.getBytes(StandardCharsets.UTF_8), second.getBytes(StandardCharsets.UTF_8));
    }

    public record AuthenticatedUser(String id, String email, String displayName) {
    }
}
