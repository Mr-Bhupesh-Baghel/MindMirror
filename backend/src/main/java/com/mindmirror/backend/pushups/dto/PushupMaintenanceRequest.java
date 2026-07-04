package com.mindmirror.backend.pushups.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class PushupMaintenanceRequest {

    @NotNull
    private LocalDate entryDate;

    @NotNull
    @Min(1)
    private Integer pushupsCount;

    @Min(1)
    private Integer challengeDay;

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public Integer getPushupsCount() {
        return pushupsCount;
    }

    public void setPushupsCount(Integer pushupsCount) {
        this.pushupsCount = pushupsCount;
    }

    public Integer getChallengeDay() {
        return challengeDay;
    }

    public void setChallengeDay(Integer challengeDay) {
        this.challengeDay = challengeDay;
    }
}
