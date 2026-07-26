package com.mindmirror.backend.tree;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "branch_item")
public class BranchItem {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "branch_id", nullable = false) private TreeBranch branch;
    @Column(name = "item_type", nullable = false) private String itemType;
    @Column(nullable = false) private String title;
    @Column(columnDefinition = "TEXT") private String content;
    @Column(nullable = false) private boolean completed;
    @Column(name = "due_on") private LocalDate dueOn;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    protected BranchItem() { }
    public BranchItem(TreeBranch branch, String itemType, String title, String content, LocalDate dueOn) { this.id = UUID.randomUUID(); this.branch = branch; this.itemType = itemType; this.title = title; this.content = content; this.dueOn = dueOn; this.createdAt = Instant.now(); }
    public UUID getId() { return id; } public String getItemType() { return itemType; } public String getTitle() { return title; } public String getContent() { return content; } public boolean isCompleted() { return completed; } public LocalDate getDueOn() { return dueOn; }
    public void toggle() { completed = !completed; }
}
