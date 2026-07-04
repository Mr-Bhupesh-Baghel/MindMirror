package com.mindmirror.backend.pushups.dto;

import java.time.LocalDate;

public class PushupMaintenanceHistoryResponse {

    private LocalDate entryDate;
    private Integer pushupsCount;
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
