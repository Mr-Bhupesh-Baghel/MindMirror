package com.mindmirror.backend.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mindmirror.backend.user.entity.User;
import com.mindmirror.backend.user.entity.UserStatus;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCaseAndStatus(String email, UserStatus status);

    boolean existsByEmailIgnoreCase(String email);
}
