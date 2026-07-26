package com.mindmirror.backend.tree;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BranchItemRepository extends JpaRepository<BranchItem, UUID> { List<BranchItem> findByBranchOrderByCreatedAtDesc(TreeBranch branch); Optional<BranchItem> findByIdAndBranch(UUID id, TreeBranch branch); }
