package com.mindmirror.backend.workout.entity;

import java.time.Instant;
import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import com.mindmirror.backend.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "workout_sessions")
public class WorkoutSession {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "exercise_name", nullable = false) private String exerciseName;
    @Column(name = "starting_number", nullable = false) private int startingNumber;
    @Column(name = "total_reps", nullable = false) private int totalReps;
    @Column(name = "completed_on", nullable = false) private LocalDate completedOn;
    @CreationTimestamp @Column(name = "created_at", nullable = false) private Instant createdAt;
    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }
    public int getStartingNumber() { return startingNumber; }
    public void setStartingNumber(int startingNumber) { this.startingNumber = startingNumber; }
    public int getTotalReps() { return totalReps; }
    public void setTotalReps(int totalReps) { this.totalReps = totalReps; }
    public LocalDate getCompletedOn() { return completedOn; }
    public void setCompletedOn(LocalDate completedOn) { this.completedOn = completedOn; }
}
