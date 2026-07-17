package com.mindmirror.backend.workout.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class WorkoutSessionRequest {
    @NotBlank @Size(max = 120) private String exerciseName;
    @Min(2) private int startingNumber;
    @Min(1) private int totalReps;
    @NotNull private LocalDate completedOn;
    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }
    public int getStartingNumber() { return startingNumber; }
    public void setStartingNumber(int startingNumber) { this.startingNumber = startingNumber; }
    public int getTotalReps() { return totalReps; }
    public void setTotalReps(int totalReps) { this.totalReps = totalReps; }
    public LocalDate getCompletedOn() { return completedOn; }
    public void setCompletedOn(LocalDate completedOn) { this.completedOn = completedOn; }
}
