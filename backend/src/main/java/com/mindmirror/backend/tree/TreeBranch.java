package com.mindmirror.backend.tree;

import com.mindmirror.backend.user.AppUser;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tree_branch")
public class TreeBranch {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private AppUser user;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "parent_id") private TreeBranch parent;
    @Column(nullable = false) private String name;
    @Column(nullable = false) private String icon;
    @Column(nullable = false) private String color;
    @Column(nullable = false) private int position;
    @Column(nullable = false) private boolean archived;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    protected TreeBranch() { }
    public TreeBranch(AppUser user, TreeBranch parent, String name, String icon, String color, int position) { this.id = UUID.randomUUID(); this.user = user; this.parent = parent; this.name = name; this.icon = icon; this.color = color; this.position = position; this.createdAt = Instant.now(); }
    public UUID getId() { return id; } public AppUser getUser() { return user; } public TreeBranch getParent() { return parent; } public String getName() { return name; } public String getIcon() { return icon; } public String getColor() { return color; } public int getPosition() { return position; } public boolean isArchived() { return archived; }
    public void update(String name, String icon, String color) { if (name != null && !name.isBlank()) this.name = name.trim(); if (icon != null && !icon.isBlank()) this.icon = icon.trim(); if (color != null && !color.isBlank()) this.color = color.trim(); }
    public void archive() { archived = true; }
}
