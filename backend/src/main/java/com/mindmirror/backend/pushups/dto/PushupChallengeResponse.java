package com.mindmirror.backend.pushups.dto;

import java.time.LocalDate;

import com.mindmirror.backend.pushups.entity.PushupStatus;

public class PushupChallengeResponse {

    private LocalDate entryDate;
    private Integer challengeDay;
    private Integer targetCount;
    private Integer completedCount;
    private PushupStatus status;
    private boolean challengeComplete;
    private int totalDays;
    private int completedDays;
    private int totalTargetPushups;
    private int totalCompletedPushups;
    private double completionRate;
    private int currentStreak;
    private int longestStreak;

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

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }

    public int getCompletedDays() {
        return completedDays;
    }

    public void setCompletedDays(int completedDays) {
        this.completedDays = completedDays;
    }

    public int getTotalTargetPushups() {
        return totalTargetPushups;
    }

    public void setTotalTargetPushups(int totalTargetPushups) {
        this.totalTargetPushups = totalTargetPushups;
    }

    public int getTotalCompletedPushups() {
        return totalCompletedPushups;
    }

    public void setTotalCompletedPushups(int totalCompletedPushups) {
        this.totalCompletedPushups = totalCompletedPushups;
    }

    public double getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(double completionRate) {
        this.completionRate = completionRate;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }
}
