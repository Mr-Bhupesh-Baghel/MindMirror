package com.mindmirror.backend.water.dto;

public class WaterStatsResponse {

    private int totalDays;
    private int goalDays;
    private int totalGlasses;
    private int currentStreak;
    private int longestStreak;
    private double averagePerDay;

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }

    public int getGoalDays() {
        return goalDays;
    }

    public void setGoalDays(int goalDays) {
        this.goalDays = goalDays;
    }

    public int getTotalGlasses() {
        return totalGlasses;
    }

    public void setTotalGlasses(int totalGlasses) {
        this.totalGlasses = totalGlasses;
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

    public double getAveragePerDay() {
        return averagePerDay;
    }

    public void setAveragePerDay(double averagePerDay) {
        this.averagePerDay = averagePerDay;
    }
}
