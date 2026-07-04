package com.mindmirror.backend.pushups.dto;

import java.time.LocalDate;

import com.mindmirror.backend.pushups.entity.PushupStatus;

public class PushupChallengeHistoryResponse {

    private LocalDate entryDate;
    private Integer challengeDay;
    private Integer targetCount;
    private Integer completedCount;
    private PushupStatus status;
    private boolean challengeComplete;

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

    public PushupStatus getStatus() {
        return status;
    }

    public void setStatus(PushupStatus status) {
        this.status = status;
    }

    public boolean isChallengeComplete() {
        return challengeComplete;
    }

    public void setChallengeComplete(boolean challengeComplete) {
        this.challengeComplete = challengeComplete;
    }
}
