package com.mindmirror.backend.water.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.mindmirror.backend.security.AuthenticatedUser;
import com.mindmirror.backend.user.entity.User;
import com.mindmirror.backend.water.dto.WaterHistoryResponse;
import com.mindmirror.backend.water.dto.WaterRequest;
import com.mindmirror.backend.water.dto.WaterResponse;
import com.mindmirror.backend.water.dto.WaterStatsResponse;
import com.mindmirror.backend.water.service.WaterService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/water")
public class WaterController {

    private final WaterService waterService;

    public WaterController(WaterService waterService) {
        this.waterService = waterService;
    }

    @GetMapping
    WaterResponse getByDate(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return waterService.getByDate(currentUser(authenticatedUser), date);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    WaterResponse save(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @Valid @RequestBody WaterRequest request
    ) {
        return waterService.save(currentUser(authenticatedUser), request);
    }

    @GetMapping("/history")
    List<WaterHistoryResponse> history(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return waterService.history(currentUser(authenticatedUser), from, to);
    }

    @GetMapping("/stats")
    WaterStatsResponse stats(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return waterService.stats(currentUser(authenticatedUser));
    }

    private User currentUser(AuthenticatedUser authenticatedUser) {
        if (authenticatedUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        return authenticatedUser.getUser();
    }
}
