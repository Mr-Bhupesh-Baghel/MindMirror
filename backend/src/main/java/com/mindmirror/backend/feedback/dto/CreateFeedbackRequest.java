package com.mindmirror.backend.feedback.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public record CreateFeedbackRequest(
    @NotBlank
    @Size(max = 120)
    String name,

    @NotBlank
    @Email
    @Size(max = 255)
    String email,

    @NotNull
    @Min(1)
    @Max(5)
    Integer rating,

    @NotBlank
    @Size(max = 5000)
    String message,

    @PastOrPresent
    LocalDate feedbackDate
) {
}
