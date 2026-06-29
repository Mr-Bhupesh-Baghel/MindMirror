package com.mindmirror.backend.auth;

import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mindmirror.backend.auth.dto.AuthResponse;
import com.mindmirror.backend.auth.dto.LoginRequest;
import com.mindmirror.backend.auth.dto.RegisterRequest;
import com.mindmirror.backend.exception.ApiException;
import com.mindmirror.backend.security.JwtService;
import com.mindmirror.backend.user.dto.UserResponse;
import com.mindmirror.backend.user.entity.User;
import com.mindmirror.backend.user.entity.UserRole;
import com.mindmirror.backend.user.entity.UserStatus;
import com.mindmirror.backend.user.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        AuthenticationManager authenticationManager,
        JwtService jwtService,
        RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "Email is already registered");
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setDisplayName(request.displayName().trim());
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        return issueTokens(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizeEmail(request.email()), request.password())
            );
        } catch (AuthenticationException exception) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        User user = userRepository.findByEmailIgnoreCaseAndStatus(normalizeEmail(request.email()), UserStatus.ACTIVE)
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        return issueTokens(user);
    }

    @Transactional
    public AuthResponse refresh(String refreshToken) {
        User user = refreshTokenService.consume(refreshToken);
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "User account is not active");
        }
        return issueTokens(user);
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
    }

    private AuthResponse issueTokens(User user) {
        return new AuthResponse(
            jwtService.createAccessToken(user),
            refreshTokenService.create(user),
            "Bearer",
            jwtService.accessTokenTtlSeconds(),
            UserResponse.from(user)
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
