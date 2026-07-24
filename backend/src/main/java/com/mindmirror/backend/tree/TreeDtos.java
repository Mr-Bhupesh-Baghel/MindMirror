package com.mindmirror.backend.tree;

import java.util.List;
public final class TreeDtos {
    private TreeDtos() { }
    public record TreeState(int totalGp, int todayGp, int currentStreak, int longestStreak, int level, String stage, int levelStartGp, Integer nextLevelGp, List<String> todayHabits, List<String> unlockedAchievements, String season) { }
}
