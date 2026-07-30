package com.mindmirror.backend.tree;

import com.mindmirror.backend.user.AppUser;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TreeService {
    private static final List<Milestone> MILESTONES = List.of(new Milestone(7, "bird"), new Milestone(14, "nest"), new Milestone(30, "butterfly"), new Milestone(60, "flower-garden"), new Milestone(100, "fireflies"), new Milestone(180, "fruits"), new Milestone(365, "golden-leaves"), new Milestone(1000, "ancient-guardian"));
    private final DailyHabitCompletionRepository completions;
    private final TreeAchievementRepository achievements;
    public TreeService(DailyHabitCompletionRepository completions, TreeAchievementRepository achievements) { this.completions = completions; this.achievements = achievements; }
    @Transactional public TreeDtos.TreeState state(AppUser user) { return stateFor(user, LocalDate.now()); }
    @Transactional public TreeDtos.TreeState toggle(AppUser user, Habit habit) { LocalDate today = LocalDate.now(); Optional<DailyHabitCompletion> completion = completions.findByUserAndHabitAndCompletedOn(user, habit, today); if (completion.isPresent()) completions.delete(completion.get()); else completions.save(new DailyHabitCompletion(user, habit, today)); return stateFor(user, today); }
    private TreeDtos.TreeState stateFor(AppUser user, LocalDate today) {
        List<DailyHabitCompletion> all = completions.findByUser(user);
        List<String> todayHabits = all.stream().filter(c -> c.getCompletedOn().equals(today)).map(c -> c.getHabit().name().toLowerCase(Locale.ROOT)).sorted().toList();
        Set<LocalDate> activeDays = all.stream().map(DailyHabitCompletion::getCompletedOn).collect(Collectors.toSet());
        int currentStreak = streakEnding(activeDays, today), longestStreak = longestStreak(activeDays);
        unlockMilestones(user, Math.max(currentStreak, longestStreak));
        Set<String> unlocked = achievements.findByUser(user).stream().map(TreeAchievement::getKey).collect(Collectors.toCollection(TreeSet::new));
        int xp = all.size() * 20;
        int level = Math.max(1, xp / 100 + 1);
        int xpToNextLevel = level * 100;
        return new TreeDtos.TreeState(currentStreak, longestStreak, todayHabits, List.copyOf(unlocked), season(today.getMonth()), xp, xpToNextLevel, level);
    }
    private void unlockMilestones(AppUser user, int streak) { Set<String> existing = achievements.findByUser(user).stream().map(TreeAchievement::getKey).collect(Collectors.toSet()); MILESTONES.stream().filter(m -> streak >= m.days && !existing.contains(m.key)).forEach(m -> achievements.save(new TreeAchievement(user, m.key))); }
    private int streakEnding(Set<LocalDate> days, LocalDate end) { int count = 0; for (LocalDate date = end; days.contains(date); date = date.minusDays(1)) count++; return count; }
    private int longestStreak(Set<LocalDate> days) { int longest = 0; for (LocalDate day : days) if (!days.contains(day.minusDays(1))) { int run = 0; for (LocalDate date = day; days.contains(date); date = date.plusDays(1)) run++; longest = Math.max(longest, run); } return longest; }
    private String season(Month month) { return switch (month) { case MARCH, APRIL, MAY -> "spring"; case JUNE, JULY, AUGUST -> "summer"; case SEPTEMBER, OCTOBER, NOVEMBER -> "autumn"; default -> "winter"; }; }
    private record Milestone(int days, String key) { }
}
