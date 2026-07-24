package com.mindmirror.backend.tree;

import com.mindmirror.backend.user.AppUser;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tree_achievement", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "achievement_key"}))
public class TreeAchievement {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private AppUser user;
    @Column(name = "achievement_key", nullable = false) private String key;
    @Column(name = "unlocked_at", nullable = false) private Instant unlockedAt;
    protected TreeAchievement() { }
    public TreeAchievement(AppUser user, String key) { this.id = UUID.randomUUID(); this.user = user; this.key = key; this.unlockedAt = Instant.now(); }
    public String getKey() { return key; }
}
