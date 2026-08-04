package com.mindmirror.backend.tree;

import java.util.List;
public final class TreeDtos {
    private TreeDtos() { }
    public record SkillProgress(String key, int completedLessons, int totalLessons, boolean unlocked, boolean completed) { }
    public record TreeState(int currentStreak, int longestStreak, List<String> unlockedAchievements, String season, int xp, int xpToNextLevel, int level, String stage, List<SkillProgress> skills) { }
}
