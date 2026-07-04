package com.mindmirror.backend.sync.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mindmirror.backend.sync.entity.UserSyncStatus;
import com.mindmirror.backend.user.entity.User;

public interface UserSyncStatusRepository extends JpaRepository<UserSyncStatus, Long> {

    Optional<UserSyncStatus> findByUser(User user);
}
