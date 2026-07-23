package com.mindmirror.backend.auth;

import com.mindmirror.backend.user.AppUser;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;
    public AuthController(AuthService auth) { this.auth = auth; }
    @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
    public AuthDtos.AuthResponse register(@Valid @RequestBody AuthDtos.RegisterRequest request) { return auth.register(request); }
    @PostMapping("/login")
    public AuthDtos.AuthResponse login(@Valid @RequestBody AuthDtos.LoginRequest request) { return auth.login(request); }
    @GetMapping("/me")
    public AuthDtos.UserResponse me(@AuthenticationPrincipal AppUser user) { return auth.user(user); }
    @PostMapping("/logout") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader("Authorization") String authorization) { auth.logout(authorization.substring(7)); }
    @ExceptionHandler(AuthService.AuthException.class) @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> authError(AuthService.AuthException e) { return Map.of("message", e.getMessage()); }
}
