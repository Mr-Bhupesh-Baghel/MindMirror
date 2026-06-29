package com.mindmirror.backend.feedback.dto;

import java.time.Instant;
import java.time.LocalDate;

import com.mindmirror.backend.feedback.entity.FeedbackEntry;

public record FeedbackResponse(
    Long id,
    Long userId,
    String name,
    String email,
    Integer rating,
    String message,
    LocalDate feedbackDate,
    Instant createdAt,
    Instant updatedAt
) {
    public static FeedbackResponse from(FeedbackEntry entry) {
        Long userId = entry.getUser() == null ? null : entry.getUser().getId();
        return new FeedbackResponse(
            entry.getId(),
            userId,
            entry.getName(),
            entry.getEmail(),
            Integer.valueOf(entry.getRating()),
            entry.getMessage(),
            entry.getFeedbackDate(),
            entry.getCreatedAt(),
            entry.getUpdatedAt()
        );
    }
}
