package com.mindmirror.backend.workout.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mindmirror.backend.user.entity.User;
import com.mindmirror.backend.workout.dto.WorkoutSessionRequest;
import com.mindmirror.backend.workout.dto.WorkoutSessionResponse;
import com.mindmirror.backend.workout.entity.WorkoutSession;
import com.mindmirror.backend.workout.repository.WorkoutSessionRepository;

@Service
public class WorkoutSessionService {
    private final WorkoutSessionRepository repository;
    public WorkoutSessionService(WorkoutSessionRepository repository) { this.repository = repository; }
    @Transactional(readOnly = true)
    public List<WorkoutSessionResponse> history(User user) { return repository.findByUserOrderByCompletedOnDescIdDesc(user).stream().map(this::response).toList(); }
    @Transactional
    public WorkoutSessionResponse create(User user, WorkoutSessionRequest request) {
        WorkoutSession session = new WorkoutSession();
        session.setUser(user); session.setExerciseName(request.getExerciseName().trim());
        session.setStartingNumber(request.getStartingNumber()); session.setTotalReps(request.getTotalReps()); session.setCompletedOn(request.getCompletedOn());
        return response(repository.save(session));
    }
    @Transactional
    public void delete(User user, Long id) { repository.deleteByIdAndUser(id, user); }
    private WorkoutSessionResponse response(WorkoutSession s) { return new WorkoutSessionResponse(s.getId(), s.getExerciseName(), s.getStartingNumber(), s.getTotalReps(), s.getCompletedOn()); }
}
