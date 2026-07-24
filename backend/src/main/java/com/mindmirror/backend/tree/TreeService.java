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
        int totalGp = all.stream().mapToInt(c -> c.getHabit().points()).sum();
        List<String> todayHabits = all.stream().filter(c -> c.getCompletedOn().equals(today)).map(c -> c.getHabit().name().toLowerCase(Locale.ROOT)).sorted().toList();
        int todayGp = all.stream().filter(c -> c.getCompletedOn().equals(today)).mapToInt(c -> c.getHabit().points()).sum();
        Set<LocalDate> activeDays = all.stream().map(DailyHabitCompletion::getCompletedOn).collect(Collectors.toSet());
        int currentStreak = streakEnding(activeDays, today), longestStreak = longestStreak(activeDays);
        unlockMilestones(user, Math.max(currentStreak, longestStreak));
        Set<String> unlocked = achievements.findByUser(user).stream().map(TreeAchievement::getKey).collect(Collectors.toCollection(TreeSet::new));
        Level level = levelFor(totalGp);
        return new TreeDtos.TreeState(totalGp, todayGp, currentStreak, longestStreak, level.number, level.stage, level.start, level.next, todayHabits, List.copyOf(unlocked), season(today.getMonth()));
    }
    private void unlockMilestones(AppUser user, int streak) { Set<String> existing = achievements.findByUser(user).stream().map(TreeAchievement::getKey).collect(Collectors.toSet()); MILESTONES.stream().filter(m -> streak >= m.days && !existing.contains(m.key)).forEach(m -> achievements.save(new TreeAchievement(user, m.key))); }
    private int streakEnding(Set<LocalDate> days, LocalDate end) { int count = 0; for (LocalDate date = end; days.contains(date); date = date.minusDays(1)) count++; return count; }
    private int longestStreak(Set<LocalDate> days) { int longest = 0; for (LocalDate day : days) if (!days.contains(day.minusDays(1))) { int run = 0; for (LocalDate date = day; days.contains(date); date = date.plusDays(1)) run++; longest = Math.max(longest, run); } return longest; }
    private Level levelFor(int gp) { if (gp < 500) return new Level(1, "Seed", 0, 500); if (gp < 1500) return new Level(2, "Sprout", 500, 1500); if (gp < 4000) return new Level(3, "Young Tree", 1500, 4000); if (gp < 9000) return new Level(4, "Healthy Tree", 4000, 9000); if (gp < 18000) return new Level(5, "Flowering Tree", 9000, 18000); if (gp < 35000) return new Level(6, "Fruit Tree", 18000, 35000); return new Level(7, "Ancient Mind Tree", 35000, null); }
    private String season(Month month) { return switch (month) { case MARCH, APRIL, MAY -> "spring"; case JUNE, JULY, AUGUST -> "summer"; case SEPTEMBER, OCTOBER, NOVEMBER -> "autumn"; default -> "winter"; }; }
    private record Milestone(int days, String key) { }
    private record Level(int number, String stage, int start, Integer next) { }
}
