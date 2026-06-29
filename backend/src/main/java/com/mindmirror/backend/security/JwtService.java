package com.mindmirror.backend.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindmirror.backend.exception.ApiException;
import com.mindmirror.backend.user.entity.User;

@Service
public class JwtService {

    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();
    private static final TypeReference<Map<String, Object>> CLAIMS_TYPE = new TypeReference<>() {
    };

    private final JwtProperties properties;
    private final ObjectMapper objectMapper;

    public JwtService(JwtProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public String createAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(properties.accessTokenTtl());

        Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("sub", user.getEmail());
        claims.put("uid", user.getId());
        claims.put("role", user.getRole().name());
        claims.put("iat", now.getEpochSecond());
        claims.put("exp", expiresAt.getEpochSecond());

        String encodedHeader = encode(header);
        String encodedClaims = encode(claims);
        String signingInput = encodedHeader + "." + encodedClaims;
        return signingInput + "." + sign(signingInput);
    }

    public JwtPrincipal parseAccessToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid access token");
            }

            if (!MessageDigest.isEqual(sign(parts[0] + "." + parts[1]).getBytes(StandardCharsets.UTF_8), parts[2].getBytes(StandardCharsets.UTF_8))) {
                throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid access token");
            }

            Map<String, Object> claims = objectMapper.readValue(BASE64_URL_DECODER.decode(parts[1]), CLAIMS_TYPE);
            long expiresAt = ((Number) claims.get("exp")).longValue();
            if (Instant.ofEpochSecond(expiresAt).isBefore(Instant.now())) {
                throw new ApiException(HttpStatus.UNAUTHORIZED, "Access token expired");
            }

            Long userId = ((Number) claims.get("uid")).longValue();
            return new JwtPrincipal(userId, (String) claims.get("sub"), (String) claims.get("role"));
        } catch (ApiException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid access token");
        }
    }

    public long accessTokenTtlSeconds() {
        return properties.accessTokenTtl().toSeconds();
    }

    private String encode(Map<String, Object> value) {
        try {
            return BASE64_URL_ENCODER.encodeToString(objectMapper.writeValueAsBytes(value));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to encode JWT", exception);
        }
    }

    private String sign(String signingInput) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(properties.secret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return BASE64_URL_ENCODER.encodeToString(mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to sign JWT", exception);
        }
    }
}
