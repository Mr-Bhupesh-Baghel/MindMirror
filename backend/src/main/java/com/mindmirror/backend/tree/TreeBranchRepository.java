package com.mindmirror.backend.tree;
import com.mindmirror.backend.user.AppUser;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TreeBranchRepository extends JpaRepository<TreeBranch, UUID> { List<TreeBranch> findByUserAndArchivedFalseOrderByPositionAsc(AppUser user); Optional<TreeBranch> findByIdAndUser(UUID id, AppUser user); }
