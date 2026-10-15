package com.mindmirror.userservice;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserServiceController {
    private final UserProfileRepository profiles;
    private final TokenService tokenService;

    public UserServiceController(UserProfileRepository profiles, TokenService tokenService) {
        this.profiles = profiles;
        this.tokenService = tokenService;
    }

    @GetMapping("/me")
    public ProfileResponse getProfile(@RequestHeader(value = "Authorization", required = false) String authorization) {
        TokenService.AuthenticatedUser identity = tokenService.requireUser(authorization);
        return responseFor(getOrCreate(identity));
    }

    @PutMapping("/me")
    public ProfileResponse updateProfile(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody ProfileUpdateRequest request) {
        TokenService.AuthenticatedUser identity = tokenService.requireUser(authorization);
        UserProfile profile = getOrCreate(identity);
        profile.setDisplayName(request.displayName().trim());
        return responseFor(profiles.save(profile));
    }

    private UserProfile getOrCreate(TokenService.AuthenticatedUser identity) {
        return profiles.findById(identity.id()).orElseGet(() ->
                profiles.save(new UserProfile(identity.id(), identity.email(), identity.displayName())));
    }

    private ProfileResponse responseFor(UserProfile profile) {
        return new ProfileResponse(profile.getUserId(), profile.getEmail(), profile.getDisplayName());
    }

    public record ProfileUpdateRequest(@NotBlank @Size(max = 120) String displayName) {
    }

    public record ProfileResponse(String id, String email, String displayName) {
    }
}
