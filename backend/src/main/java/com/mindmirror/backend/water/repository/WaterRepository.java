package com.mindmirror.backend.water.repository;

import com.mindmirror.backend.water.entity.WaterEntry;
import com.mindmirror.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WaterRepository extends JpaRepository<WaterEntry, Long> {

    Optional<WaterEntry> findByUserAndEntryDate(
            User user,
            LocalDate entryDate
    );

    List<WaterEntry> findByUserOrderByEntryDateDesc(User user);

    boolean existsByUserAndEntryDate(
            User user,
            LocalDate entryDate
    );

    List<WaterEntry> findByUserAndEntryDateBetween(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );

    List<WaterEntry> findByUserAndEntryDateBetweenOrderByEntryDateDesc(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );
}
