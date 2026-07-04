package com.mindmirror.backend.pushups.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mindmirror.backend.pushups.entity.MaintenanceEntry;
import com.mindmirror.backend.user.entity.User;

public interface MaintenanceRepository extends JpaRepository<MaintenanceEntry, Long> {

    Optional<MaintenanceEntry> findByUserAndEntryDate(User user, java.time.LocalDate entryDate);

    Optional<MaintenanceEntry> findFirstByUserOrderByEntryDateDesc(User user);

    List<MaintenanceEntry> findByUserOrderByEntryDateDesc(User user);
}
