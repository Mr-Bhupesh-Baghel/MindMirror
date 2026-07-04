package com.mindmirror.backend.routine.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class RoutineCompletionRequest {

    @NotNull
    private LocalDate completionDate;

    @Valid
    @NotEmpty
    private List<RoutineCompletionItemRequest> completions = new ArrayList<>();

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public List<RoutineCompletionItemRequest> getCompletions() {
        return completions;
    }

    public void setCompletions(List<RoutineCompletionItemRequest> completions) {
        this.completions = completions;
    }
}
