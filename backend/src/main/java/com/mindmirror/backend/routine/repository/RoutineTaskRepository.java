package com.mindmirror.backend.routine.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mindmirror.backend.routine.entity.RoutineTask;
import com.mindmirror.backend.user.entity.User;

public interface RoutineTaskRepository extends JpaRepository<RoutineTask, Long> {

    List<RoutineTask> findByUserAndActiveTrueOrderByCategoryAscSortOrderAscIdAsc(User user);

    Optional<RoutineTask> findByIdAndUser(Long id, User user);

    Optional<RoutineTask> findByUserAndTitleAndCategory(User user, String title, String category);
}
