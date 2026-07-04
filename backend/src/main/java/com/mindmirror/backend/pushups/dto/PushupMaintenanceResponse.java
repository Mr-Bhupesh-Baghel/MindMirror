package com.mindmirror.backend.pushups.dto;

import java.time.LocalDate;

public class PushupMaintenanceResponse {

    private LocalDate entryDate;
    private Integer pushupsCount;
    private Integer challengeDay;
    private int totalDays;
    private int totalPushups;
    private double averagePerDay;
    private int currentStreak;
    private int longestStreak;

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

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }

    public int getTotalPushups() {
        return totalPushups;
    }

    public void setTotalPushups(int totalPushups) {
        this.totalPushups = totalPushups;
    }

    public double getAveragePerDay() {
        return averagePerDay;
    }

    public void setAveragePerDay(double averagePerDay) {
        this.averagePerDay = averagePerDay;
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
