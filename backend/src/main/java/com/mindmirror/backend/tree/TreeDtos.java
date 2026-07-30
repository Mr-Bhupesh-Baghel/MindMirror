package com.mindmirror.backend.tree;

import java.util.List;
public final class TreeDtos {
    private TreeDtos() { }
    public record TreeState(int currentStreak, int longestStreak, List<String> todayHabits, List<String> unlockedAchievements, String season, int xp, int xpToNextLevel, int level) { }
}
