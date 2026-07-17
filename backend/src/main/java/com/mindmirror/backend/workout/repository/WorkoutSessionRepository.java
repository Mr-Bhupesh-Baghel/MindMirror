package com.mindmirror.backend.workout.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mindmirror.backend.user.entity.User;
import com.mindmirror.backend.workout.entity.WorkoutSession;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {
    List<WorkoutSession> findByUserOrderByCompletedOnDescIdDesc(User user);
    void deleteByIdAndUser(Long id, User user);
}
