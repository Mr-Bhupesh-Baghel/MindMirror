package com.mindmirror.backend.routine.dto;

import jakarta.validation.constraints.NotNull;

public class RoutineCompletionItemRequest {

    @NotNull
    private Long taskId;

    @NotNull
    private Boolean completed;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
