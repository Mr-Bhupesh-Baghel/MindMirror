package com.mindmirror.backend.workout.dto;

import java.time.LocalDate;

public record WorkoutSessionResponse(Long id, String exerciseName, int startingNumber, int totalReps, LocalDate completedOn) { }
