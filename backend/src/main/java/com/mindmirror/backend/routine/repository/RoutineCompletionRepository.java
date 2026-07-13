package com.mindmirror.backend.routine.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mindmirror.backend.routine.entity.RoutineCompletion;
import com.mindmirror.backend.routine.entity.RoutineTask;
import com.mindmirror.backend.user.entity.User;

public interface RoutineCompletionRepository extends JpaRepository<RoutineCompletion, Long> {

    Optional<RoutineCompletion> findByUserAndTaskAndCompletionDate(User user, RoutineTask task, LocalDate completionDate);

    List<RoutineCompletion> findByUserAndCompletionDate(User user, LocalDate completionDate);

    List<RoutineCompletion> findByUserAndCompletionDateBetweenOrderByCompletionDateDesc(User user, LocalDate from, LocalDate to);

    long countByUserAndCompletedTrue(User user);

    List<RoutineCompletion> findByUserAndCompletedTrue(User user);
}
