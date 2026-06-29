package com.mindmirror.backend.auth.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.mindmirror.backend.auth.entity.RefreshToken;
import com.mindmirror.backend.user.entity.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("update RefreshToken token set token.revokedAt = :revokedAt where token.user = :user and token.revokedAt is null")
    void revokeAllForUser(User user, Instant revokedAt);
}
