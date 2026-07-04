package com.mindmirror.backend.pushups.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class PushupChallengeRequest {

    @NotNull
    private LocalDate entryDate;

    @NotNull
    @Min(1)
    private Integer challengeDay;

    @Min(1)
    private Integer targetCount;

    @NotNull
    @Min(0)
    private Integer completedCount;

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public Integer getChallengeDay() {
        return challengeDay;
    }

    public void setChallengeDay(Integer challengeDay) {
        this.challengeDay = challengeDay;
    }

    public Integer getTargetCount() {
        return targetCount;
    }

    public void setTargetCount(Integer targetCount) {
        this.targetCount = targetCount;
    }

    public Integer getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(Integer completedCount) {
        this.completedCount = completedCount;
    }
}
