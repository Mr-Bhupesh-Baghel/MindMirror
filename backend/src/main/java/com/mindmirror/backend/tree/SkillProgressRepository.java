package com.mindmirror.backend.tree;

import com.mindmirror.backend.user.AppUser;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillProgressRepository extends JpaRepository<SkillProgress, UUID> { List<SkillProgress> findByUser(AppUser user); }
