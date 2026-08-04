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
    private static final List<SkillDefinition> SKILLS = List.of(
        new SkillDefinition("dictation", 8, 0), new SkillDefinition("vocabulary", 6, 1), new SkillDefinition("reading", 6, 2),
        new SkillDefinition("writing", 5, 3), new SkillDefinition("grammar", 8, 4), new SkillDefinition("speaking", 5, 5), new SkillDefinition("quiz", 5, 6));
    private final SkillProgressRepository progress;
    private final TreeAchievementRepository achievements;
    public TreeService(SkillProgressRepository progress, TreeAchievementRepository achievements) { this.progress = progress; this.achievements = achievements; }
    @Transactional public TreeDtos.TreeState state(AppUser user) { return stateFor(user, LocalDate.now()); }
    @Transactional public TreeDtos.TreeState completeLesson(AppUser user, String skillKey) {
        SkillDefinition skill = SKILLS.stream().filter(item -> item.key.equals(skillKey)).findFirst().orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Skill not found"));
        Map<String, SkillProgress> all = progress.findByUser(user).stream().collect(java.util.stream.Collectors.toMap(SkillProgress::getSkillKey, item -> item));
        if (skill.unlockOrder > 0) {
            SkillDefinition previous = SKILLS.get(skill.unlockOrder - 1);
            SkillProgress previousProgress = all.get(previous.key);
            if (previousProgress == null || previousProgress.getCompletedLessons() < previous.totalLessons) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "Complete the previous skill to unlock this lesson");
        }
        SkillProgress entry = all.get(skill.key);
        if (entry == null) entry = progress.save(new SkillProgress(user, skill.key, skill.totalLessons));
        entry.completeLesson();
        return stateFor(user, LocalDate.now());
    }
    private TreeDtos.TreeState stateFor(AppUser user, LocalDate today) {
        List<SkillProgress> all = progress.findByUser(user);
        Set<LocalDate> activeDays = all.stream().filter(item -> item.getLastCompletedOn() != null).map(SkillProgress::getLastCompletedOn).collect(java.util.stream.Collectors.toSet());
        int currentStreak = streakEnding(activeDays, today), longestStreak = longestStreak(activeDays);
        unlockMilestones(user, Math.max(currentStreak, longestStreak));
        Set<String> unlocked = achievements.findByUser(user).stream().map(TreeAchievement::getKey).collect(Collectors.toCollection(TreeSet::new));
        int lessonsCompleted = all.stream().mapToInt(SkillProgress::getCompletedLessons).sum();
        int xp = lessonsCompleted * 20;
        int level = Math.max(1, xp / 100 + 1);
        int xpToNextLevel = level * 100;
        Map<String, SkillProgress> byKey = all.stream().collect(java.util.stream.Collectors.toMap(SkillProgress::getSkillKey, item -> item));
        List<TreeDtos.SkillProgress> skills = SKILLS.stream().map(skill -> {
            SkillProgress item = byKey.get(skill.key);
            int completed = item == null ? 0 : item.getCompletedLessons();
            boolean unlockedSkill = skill.unlockOrder == 0 || (byKey.containsKey(SKILLS.get(skill.unlockOrder - 1).key) && byKey.get(SKILLS.get(skill.unlockOrder - 1).key).getCompletedLessons() >= SKILLS.get(skill.unlockOrder - 1).totalLessons);
            return new TreeDtos.SkillProgress(skill.key, completed, skill.totalLessons, unlockedSkill, completed >= skill.totalLessons);
        }).toList();
        return new TreeDtos.TreeState(currentStreak, longestStreak, List.copyOf(unlocked), season(today.getMonth()), xp, xpToNextLevel, level, stage(lessonsCompleted), skills);
    }
    private void unlockMilestones(AppUser user, int streak) { Set<String> existing = achievements.findByUser(user).stream().map(TreeAchievement::getKey).collect(Collectors.toSet()); MILESTONES.stream().filter(m -> streak >= m.days && !existing.contains(m.key)).forEach(m -> achievements.save(new TreeAchievement(user, m.key))); }
    private int streakEnding(Set<LocalDate> days, LocalDate end) { int count = 0; for (LocalDate date = end; days.contains(date); date = date.minusDays(1)) count++; return count; }
    private int longestStreak(Set<LocalDate> days) { int longest = 0; for (LocalDate day : days) if (!days.contains(day.minusDays(1))) { int run = 0; for (LocalDate date = day; days.contains(date); date = date.plusDays(1)) run++; longest = Math.max(longest, run); } return longest; }
    private String season(Month month) { return switch (month) { case MARCH, APRIL, MAY -> "spring"; case JUNE, JULY, AUGUST -> "summer"; case SEPTEMBER, OCTOBER, NOVEMBER -> "autumn"; default -> "winter"; }; }
    private String stage(int lessons) { if (lessons == 0) return "seedling"; if (lessons < 6) return "sapling"; if (lessons < 16) return "young-tree"; if (lessons < 30) return "flourishing"; return "abundant"; }
    private record SkillDefinition(String key, int totalLessons, int unlockOrder) { }
    private record Milestone(int days, String key) { }
}
