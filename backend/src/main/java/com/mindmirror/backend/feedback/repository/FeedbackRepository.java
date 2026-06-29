package com.mindmirror.backend.feedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mindmirror.backend.feedback.entity.FeedbackEntry;

public interface FeedbackRepository extends JpaRepository<FeedbackEntry, Long> {
}
