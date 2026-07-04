package com.mindmirror.backend.pushups.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mindmirror.backend.pushups.entity.PushupEntry;
import com.mindmirror.backend.user.entity.User;

public interface PushupRepository extends JpaRepository<PushupEntry, Long> {

    Optional<PushupEntry> findByUserAndChallengeDay(User user, Integer challengeDay);

    Optional<PushupEntry> findFirstByUserOrderByChallengeDayDesc(User user);

    List<PushupEntry> findByUserOrderByChallengeDayDesc(User user);
}
