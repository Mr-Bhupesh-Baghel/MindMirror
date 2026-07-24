package com.mindmirror.backend.tree;

import com.mindmirror.backend.user.AppUser;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "daily_habit_completion", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "habit_key", "completed_on"}))
public class DailyHabitCompletion {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private AppUser user;
    @Enumerated(EnumType.STRING) @Column(name = "habit_key", nullable = false) private Habit habit;
    @Column(name = "completed_on", nullable = false) private LocalDate completedOn;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    protected DailyHabitCompletion() { }
    public DailyHabitCompletion(AppUser user, Habit habit, LocalDate completedOn) { this.id = UUID.randomUUID(); this.user = user; this.habit = habit; this.completedOn = completedOn; this.createdAt = Instant.now(); }
    public Habit getHabit() { return habit; }
    public LocalDate getCompletedOn() { return completedOn; }
}
