package com.mindmirror.backend.user;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mindmirror.backend.security.AuthenticatedUser;
import com.mindmirror.backend.user.dto.UpdateUserRequest;
import com.mindmirror.backend.user.dto.UserResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    UserResponse me(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return userService.getProfile(authenticatedUser.getUser());
    }

    @PutMapping("/me")
    UserResponse updateMe(@AuthenticationPrincipal AuthenticatedUser authenticatedUser, @Valid @RequestBody UpdateUserRequest request) {
        return userService.updateProfile(authenticatedUser.getUser(), request);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteMe(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        userService.deleteAccount(authenticatedUser.getUser());
    }
}
