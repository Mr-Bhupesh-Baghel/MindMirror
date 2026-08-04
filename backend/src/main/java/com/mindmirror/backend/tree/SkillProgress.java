package com.mindmirror.backend.tree;

import com.mindmirror.backend.user.AppUser;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "skill_progress", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "skill_key"}))
public class SkillProgress {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private AppUser user;
    @Column(name = "skill_key", nullable = false) private String skillKey;
    @Column(name = "completed_lessons", nullable = false) private int completedLessons;
    @Column(name = "total_lessons", nullable = false) private int totalLessons;
    @Column(name = "last_completed_on") private LocalDate lastCompletedOn;
    protected SkillProgress() { }
    public SkillProgress(AppUser user, String skillKey, int totalLessons) { this.id = UUID.randomUUID(); this.user = user; this.skillKey = skillKey; this.totalLessons = totalLessons; }
    public void completeLesson() { if (completedLessons < totalLessons) completedLessons++; lastCompletedOn = LocalDate.now(); }
    public String getSkillKey() { return skillKey; }
    public int getCompletedLessons() { return completedLessons; }
    public LocalDate getLastCompletedOn() { return lastCompletedOn; }
}
