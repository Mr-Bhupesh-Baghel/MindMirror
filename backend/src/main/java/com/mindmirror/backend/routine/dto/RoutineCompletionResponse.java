package com.mindmirror.backend.routine.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RoutineCompletionResponse {

    private LocalDate completionDate;
    private int totalTasks;
    private int completedTasks;
    private double completionRate;
    private List<RoutineCompletionItemResponse> tasks = new ArrayList<>();

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
    }

    public double getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(double completionRate) {
        this.completionRate = completionRate;
    }

    public List<RoutineCompletionItemResponse> getTasks() {
        return tasks;
    }

    public void setTasks(List<RoutineCompletionItemResponse> tasks) {
        this.tasks = tasks;
    }
}
