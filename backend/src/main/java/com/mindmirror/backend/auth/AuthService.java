package com.mindmirror.backend.auth;

import com.mindmirror.backend.user.AppUser;
import com.mindmirror.backend.user.AppUserRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Locale;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final AppUserRepository users;
    private final AuthSessionRepository sessions;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();
    private final long tokenDays;

    public AuthService(AppUserRepository users, AuthSessionRepository sessions, PasswordEncoder passwordEncoder,
                       @Value("${app.auth.token-days:30}") long tokenDays) {
        this.users = users; this.sessions = sessions; this.passwordEncoder = passwordEncoder; this.tokenDays = tokenDays;
    }
    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (users.findByEmail(email).isPresent()) throw new AuthException("An account already exists for this email.");
        AppUser user = users.save(new AppUser(UUID.randomUUID(), email, request.displayName().trim(), passwordEncoder.encode(request.password())));
        return issue(user);
    }
    @Transactional
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        AppUser user = users.findByEmail(request.email().trim().toLowerCase(Locale.ROOT))
            .filter(candidate -> passwordEncoder.matches(request.password(), candidate.getPasswordHash()))
            .orElseThrow(() -> new AuthException("Email or password is incorrect."));
        return issue(user);
    }
    @Transactional
    public AuthDtos.AuthResponse issue(AppUser user) {
        byte[] bytes = new byte[32]; secureRandom.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        sessions.save(new AuthSession(user, hash(token), Instant.now().plus(tokenDays, ChronoUnit.DAYS)));
        return new AuthDtos.AuthResponse(token, user(user));
    }
    public AppUser findUser(String token) {
        return sessions.findByTokenHash(hash(token)).filter(s -> s.getExpiresAt().isAfter(Instant.now()))
            .map(AuthSession::getUser).orElse(null);
    }
    @Transactional public void logout(String token) { sessions.deleteByTokenHash(hash(token)); }
    public AuthDtos.UserResponse user(AppUser user) { return new AuthDtos.UserResponse(user.getId().toString(), user.getEmail(), user.getDisplayName()); }
    private String hash(String value) {
        try { return Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8))); }
        catch (Exception e) { throw new IllegalStateException("Unable to hash token", e); }
    }
    public static class AuthException extends RuntimeException { public AuthException(String message) { super(message); } }
}
