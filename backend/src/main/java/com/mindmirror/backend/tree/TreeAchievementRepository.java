package com.mindmirror.backend.tree;

import com.mindmirror.backend.user.AppUser;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreeAchievementRepository extends JpaRepository<TreeAchievement, UUID> { List<TreeAchievement> findByUser(AppUser user); }
