package com.mindmirror.backend.tree;

import com.mindmirror.backend.user.AppUser;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyHabitCompletionRepository extends JpaRepository<DailyHabitCompletion, UUID> {
    List<DailyHabitCompletion> findByUser(AppUser user);
    Optional<DailyHabitCompletion> findByUserAndHabitAndCompletedOn(AppUser user, Habit habit, LocalDate completedOn);
}
