package com.mindmirror.backend.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mindmirror.backend.auth.entity.RefreshToken;
import com.mindmirror.backend.auth.repository.RefreshTokenRepository;
import com.mindmirror.backend.exception.ApiException;
import com.mindmirror.backend.security.JwtProperties;
import com.mindmirror.backend.user.entity.User;

@Service
public class RefreshTokenService {

    private final SecureRandom secureRandom = new SecureRandom();
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtProperties jwtProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public String create(User user) {
        byte[] tokenBytes = new byte[64];
        secureRandom.nextBytes(tokenBytes);
        String tokenValue = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hash(tokenValue));
        refreshToken.setExpiresAt(Instant.now().plus(jwtProperties.refreshTokenTtl()));
        refreshTokenRepository.save(refreshToken);

        return tokenValue;
    }

    @Transactional
    public User consume(String tokenValue) {
        Instant now = Instant.now();
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(hash(tokenValue))
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        if (!refreshToken.isActive(now)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token expired or revoked");
        }

        refreshToken.setRevokedAt(now);
        return refreshToken.getUser();
    }

    @Transactional
    public void revoke(String tokenValue) {
        refreshTokenRepository.findByTokenHash(hash(tokenValue))
            .ifPresent(refreshToken -> refreshToken.setRevokedAt(Instant.now()));
    }

    @Transactional
    public void revokeAll(User user) {
        refreshTokenRepository.revokeAllForUser(user, Instant.now());
    }

    private String hash(String tokenValue) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(tokenValue.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to hash refresh token", exception);
        }
    }
}
