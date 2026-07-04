package com.mindmirror.backend.affirmation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AffirmationRequest {

    @NotBlank
    @Size(max = 500)
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
