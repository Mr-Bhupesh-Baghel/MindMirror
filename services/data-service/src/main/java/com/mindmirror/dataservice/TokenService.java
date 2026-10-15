package com.mindmirror.dataservice;

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

    public TokenService(@Value("${mindmirror.token-secret}") String secret) {
        this.secret = secret;
    }

    public String requireUserId(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
        }
        String[] parts = authorization.substring("Bearer ".length()).trim().split("\\.", -1);
        String payload = parts.length == 5 ? parts[0] + "." + parts[1] + "." + parts[2] + "." + parts[3] : "";
        if (parts.length != 5 || !java.security.MessageDigest.isEqual(
                parts[4].getBytes(StandardCharsets.UTF_8), sign(payload).getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication token");
        }
        try {
            if (Long.parseLong(parts[1]) <= Instant.now().getEpochSecond()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication token has expired");
            }
            UUID.fromString(parts[0]);
            Base64.getUrlDecoder().decode(parts[2]);
            Base64.getUrlDecoder().decode(parts[3]);
            return parts[0];
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
            throw new IllegalStateException("Could not verify authentication token", exception);
        }
    }
}
