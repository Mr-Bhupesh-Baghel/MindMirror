package com.mindmirror.backend.affirmation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mindmirror.backend.affirmation.entity.Affirmation;
import com.mindmirror.backend.user.entity.User;

public interface AffirmationRepository extends JpaRepository<Affirmation, Long> {

    List<Affirmation> findByUserAndActiveTrueOrderByCreatedAtAsc(User user);

    Optional<Affirmation> findByIdAndUser(Long id, User user);

    Optional<Affirmation> findByUserAndText(User user, String text);
}
