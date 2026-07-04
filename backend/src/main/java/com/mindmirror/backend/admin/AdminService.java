package com.mindmirror.backend.admin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.mindmirror.backend.admin.dto.AdminStatsResponse;
import com.mindmirror.backend.admin.dto.AdminUserResponse;
import com.mindmirror.backend.admin.dto.AdminUserUpdateRequest;
import com.mindmirror.backend.admin.dto.RetentionStats;
import com.mindmirror.backend.admin.dto.RoutineCompletionStats;
import com.mindmirror.backend.admin.dto.StreakStats;
import com.mindmirror.backend.exception.ApiException;
import com.mindmirror.backend.feedback.entity.FeedbackEntry;
import com.mindmirror.backend.feedback.repository.FeedbackRepository;
import com.mindmirror.backend.pushups.entity.MaintenanceEntry;
import com.mindmirror.backend.pushups.entity.PushupEntry;
import com.mindmirror.backend.pushups.repository.MaintenanceRepository;
import com.mindmirror.backend.pushups.repository.PushupRepository;
import com.mindmirror.backend.routine.entity.RoutineCompletion;
import com.mindmirror.backend.routine.repository.RoutineCompletionRepository;
import com.mindmirror.backend.user.entity.User;
import com.mindmirror.backend.user.entity.UserStatus;
import com.mindmirror.backend.user.repository.UserRepository;
import com.mindmirror.backend.water.entity.WaterEntry;
import com.mindmirror.backend.water.repository.WaterRepository;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;
    private final WaterRepository waterRepository;
    private final PushupRepository pushupRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final RoutineCompletionRepository routineCompletionRepository;

    public AdminService(
        UserRepository userRepository,
        FeedbackRepository feedbackRepository,
        WaterRepository waterRepository,
        PushupRepository pushupRepository,
        MaintenanceRepository maintenanceRepository,
        RoutineCompletionRepository routineCompletionRepository
    ) {
        this.userRepository = userRepository;
        this.feedbackRepository = feedbackRepository;
        this.waterRepository = waterRepository;
        this.pushupRepository = pushupRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.routineCompletionRepository = routineCompletionRepository;
    }

    @Transactional(readOnly = true)
    public Page<AdminUserResponse> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(AdminUserResponse::from);
    }

    @Transactional
    public AdminUserResponse updateUser(Long id, AdminUserUpdateRequest request) {
        User user = findUser(id);

        if (StringUtils.hasText(request.displayName())) {
            user.setDisplayName(request.displayName().trim());
        }
        if (request.role() != null) {
            user.setRole(request.role());
        }
        if (request.status() != null) {
            user.setStatus(request.status());
        }

        return AdminUserResponse.from(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = findUser(id);
        user.setStatus(UserStatus.DELETED);
        user.setEmail("deleted-" + user.getId() + "@deleted.local");
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public AdminStatsResponse stats() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        List<User> users = userRepository.findAll();
        List<WaterEntry> waterEntries = waterRepository.findAll();
        List<PushupEntry> pushupEntries = pushupRepository.findAll();
        List<MaintenanceEntry> maintenanceEntries = maintenanceRepository.findAll();
        List<RoutineCompletion> routineCompletions = routineCompletionRepository.findAll();
        List<FeedbackEntry> feedbackEntries = feedbackRepository.findAll();

        Set<Long> dailyActiveUsers = activeUsersOn(today, waterEntries, pushupEntries, maintenanceEntries, routineCompletions, feedbackEntries);
        Map<Long, Set<LocalDate>> allActivityDates = activityDates(waterEntries, pushupEntries, maintenanceEntries, routineCompletions, feedbackEntries);

        long activeUsers = users.stream().filter(user -> user.getStatus() == UserStatus.ACTIVE).count();
        long deletedUsers = users.stream().filter(user -> user.getStatus() == UserStatus.DELETED).count();

        return new AdminStatsResponse(
            users.size(),
            activeUsers,
            deletedUsers,
            dailyActiveUsers.size(),
            streakStats(waterGoalDates(waterEntries), today),
            streakStats(pushupGoalDates(pushupEntries, maintenanceEntries), today),
            routineCompletionStats(routineCompletions),
            retentionStats(users, allActivityDates, today),
            Instant.now()
        );
    }

    @Transactional(readOnly = true)
    public byte[] export(String dataset, String format) {
        validateFormat(format);
        List<List<String>> rows = exportRows(dataset);
        if ("xlsx".equalsIgnoreCase(format)) {
            return xlsx(rows);
        }
        return csv(rows).getBytes(StandardCharsets.UTF_8);
    }

    public String exportFilename(String dataset, String format) {
        validateFormat(format);
        String normalizedDataset = normalizeDataset(dataset);
        String extension = "xlsx".equalsIgnoreCase(format) ? "xlsx" : "csv";
        return "mindmirror-admin-" + normalizedDataset + "." + extension;
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private Set<Long> activeUsersOn(
        LocalDate date,
        List<WaterEntry> waterEntries,
        List<PushupEntry> pushupEntries,
        List<MaintenanceEntry> maintenanceEntries,
        List<RoutineCompletion> routineCompletions,
        List<FeedbackEntry> feedbackEntries
    ) {
        Set<Long> activeUsers = new HashSet<>();
        waterEntries.stream()
            .filter(entry -> date.equals(entry.getEntryDate()))
            .map(entry -> entry.getUser().getId())
            .forEach(activeUsers::add);
        pushupEntries.stream()
            .filter(entry -> date.equals(entry.getEntryDate()))
            .map(entry -> entry.getUser().getId())
            .forEach(activeUsers::add);
        maintenanceEntries.stream()
            .filter(entry -> date.equals(entry.getEntryDate()))
            .map(entry -> entry.getUser().getId())
            .forEach(activeUsers::add);
        routineCompletions.stream()
            .filter(entry -> date.equals(entry.getCompletionDate()))
            .map(entry -> entry.getUser().getId())
            .forEach(activeUsers::add);
        feedbackEntries.stream()
            .filter(entry -> entry.getUser() != null)
            .filter(entry -> date.equals(entry.getFeedbackDate()))
            .map(entry -> entry.getUser().getId())
            .forEach(activeUsers::add);
        return activeUsers;
    }

    private Map<Long, Set<LocalDate>> activityDates(
        List<WaterEntry> waterEntries,
        List<PushupEntry> pushupEntries,
        List<MaintenanceEntry> maintenanceEntries,
        List<RoutineCompletion> routineCompletions,
        List<FeedbackEntry> feedbackEntries
    ) {
        Map<Long, Set<LocalDate>> dates = new HashMap<>();
        waterEntries.forEach(entry -> addDate(dates, entry.getUser().getId(), entry.getEntryDate()));
        pushupEntries.forEach(entry -> addDate(dates, entry.getUser().getId(), entry.getEntryDate()));
        maintenanceEntries.forEach(entry -> addDate(dates, entry.getUser().getId(), entry.getEntryDate()));
        routineCompletions.forEach(entry -> addDate(dates, entry.getUser().getId(), entry.getCompletionDate()));
        feedbackEntries.stream()
            .filter(entry -> entry.getUser() != null)
            .forEach(entry -> addDate(dates, entry.getUser().getId(), entry.getFeedbackDate()));
        return dates;
    }

    private Map<Long, Set<LocalDate>> waterGoalDates(List<WaterEntry> waterEntries) {
        Map<Long, Set<LocalDate>> dates = new HashMap<>();
        waterEntries.stream()
            .filter(entry -> entry.getGlassesCount() >= entry.getGoalGlasses())
            .forEach(entry -> addDate(dates, entry.getUser().getId(), entry.getEntryDate()));
        return dates;
    }

    private Map<Long, Set<LocalDate>> pushupGoalDates(List<PushupEntry> pushupEntries, List<MaintenanceEntry> maintenanceEntries) {
        Map<Long, Set<LocalDate>> dates = new HashMap<>();
        pushupEntries.stream()
            .filter(entry -> entry.getCompletedCount() >= entry.getTargetCount())
            .forEach(entry -> addDate(dates, entry.getUser().getId(), entry.getEntryDate()));
        maintenanceEntries.stream()
            .filter(entry -> entry.getPushupsCount() > 0)
            .forEach(entry -> addDate(dates, entry.getUser().getId(), entry.getEntryDate()));
        return dates;
    }

    private void addDate(Map<Long, Set<LocalDate>> dates, Long userId, LocalDate date) {
        dates.computeIfAbsent(userId, key -> new HashSet<>()).add(date);
    }

    private StreakStats streakStats(Map<Long, Set<LocalDate>> userDates, LocalDate today) {
        if (userDates.isEmpty()) {
            return new StreakStats(0, 0, 0);
        }

        List<Integer> currentStreaks = new ArrayList<>();
        int longest = 0;

        for (Set<LocalDate> dates : userDates.values()) {
            int current = currentStreak(dates, today);
            currentStreaks.add(current);
            longest = Math.max(longest, longestStreak(dates));
        }

        int averageCurrent = (int) Math.round(currentStreaks.stream().mapToInt(Integer::intValue).average().orElse(0));
        int longestCurrent = currentStreaks.stream().mapToInt(Integer::intValue).max().orElse(0);
        return new StreakStats(averageCurrent, longestCurrent, longest);
    }

    private int currentStreak(Set<LocalDate> dates, LocalDate today) {
        int streak = 0;
        LocalDate cursor = today;
        while (dates.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    private int longestStreak(Set<LocalDate> dates) {
        List<LocalDate> sortedDates = dates.stream().sorted().toList();
        int current = 0;
        int longest = 0;
        LocalDate previous = null;
        for (LocalDate date : sortedDates) {
            current = previous != null && previous.plusDays(1).equals(date) ? current + 1 : 1;
            longest = Math.max(longest, current);
            previous = date;
        }
        return longest;
    }

    private RoutineCompletionStats routineCompletionStats(List<RoutineCompletion> completions) {
        long completedEntries = completions.stream().filter(RoutineCompletion::isCompleted).count();
        double completionRate = completions.isEmpty() ? 0 : roundPercent(completedEntries, completions.size());
        return new RoutineCompletionStats(completions.size(), completedEntries, completionRate);
    }

    private RetentionStats retentionStats(List<User> users, Map<Long, Set<LocalDate>> activityDates, LocalDate today) {
        LocalDate cutoff = today.minusDays(7);
        long eligibleUsers = 0;
        long retainedUsers = 0;

        for (User user : users) {
            LocalDate createdDate = user.getCreatedAt().atZone(ZoneOffset.UTC).toLocalDate();
            if (createdDate.isAfter(cutoff)) {
                continue;
            }
            eligibleUsers++;
            Set<LocalDate> dates = activityDates.getOrDefault(user.getId(), Set.of());
            boolean retained = dates.stream().anyMatch(date -> !date.isBefore(cutoff));
            if (retained) {
                retainedUsers++;
            }
        }

        return new RetentionStats(eligibleUsers, retainedUsers, eligibleUsers == 0 ? 0 : roundPercent(retainedUsers, eligibleUsers));
    }

    private double roundPercent(long numerator, long denominator) {
        return Math.round((numerator * 10000.0) / denominator) / 100.0;
    }

    private List<List<String>> exportRows(String dataset) {
        return switch (normalizeDataset(dataset)) {
            case "feedback" -> feedbackRows();
            case "stats" -> statsRows();
            default -> userRows();
        };
    }

    private String normalizeDataset(String dataset) {
        if (!StringUtils.hasText(dataset)) {
            return "users";
        }
        String normalized = dataset.trim().toLowerCase(Locale.ROOT);
        if (!List.of("users", "feedback", "stats").contains(normalized)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Export dataset must be users, feedback, or stats");
        }
        return normalized;
    }

    private void validateFormat(String format) {
        if (!"csv".equalsIgnoreCase(format) && !"xlsx".equalsIgnoreCase(format)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Export format must be csv or xlsx");
        }
    }

    private List<List<String>> userRows() {
        List<List<String>> rows = new ArrayList<>();
        rows.add(List.of("id", "email", "displayName", "role", "status", "createdAt", "updatedAt"));
        userRepository.findAll().stream()
            .sorted(Comparator.comparing(User::getId))
            .map(AdminUserResponse::from)
            .map(user -> List.of(
                user.id().toString(),
                user.email(),
                user.displayName(),
                user.role().name(),
                user.status().name(),
                user.createdAt().toString(),
                user.updatedAt().toString()
            ))
            .forEach(rows::add);
        return rows;
    }

    private List<List<String>> feedbackRows() {
        List<List<String>> rows = new ArrayList<>();
        rows.add(List.of("id", "userId", "name", "email", "rating", "message", "feedbackDate", "createdAt", "updatedAt"));
        feedbackRepository.findAll().stream()
            .sorted(Comparator.comparing(FeedbackEntry::getCreatedAt).reversed())
            .map(entry -> List.of(
                entry.getId().toString(),
                entry.getUser() == null ? "" : entry.getUser().getId().toString(),
                entry.getName(),
                entry.getEmail(),
                entry.getRating().toString(),
                entry.getMessage(),
                entry.getFeedbackDate().toString(),
                entry.getCreatedAt().toString(),
                entry.getUpdatedAt().toString()
            ))
            .forEach(rows::add);
        return rows;
    }

    private List<List<String>> statsRows() {
        AdminStatsResponse stats = stats();
        Map<String, String> values = new LinkedHashMap<>();
        values.put("totalUsers", Long.toString(stats.totalUsers()));
        values.put("activeUsers", Long.toString(stats.activeUsers()));
        values.put("deletedUsers", Long.toString(stats.deletedUsers()));
        values.put("dailyActiveUsers", Long.toString(stats.dailyActiveUsers()));
        values.put("waterAverageCurrentStreak", Integer.toString(stats.waterStreaks().averageCurrentStreak()));
        values.put("waterLongestCurrentStreak", Integer.toString(stats.waterStreaks().longestCurrentStreak()));
        values.put("waterLongestStreak", Integer.toString(stats.waterStreaks().longestStreak()));
        values.put("pushupAverageCurrentStreak", Integer.toString(stats.pushupStreaks().averageCurrentStreak()));
        values.put("pushupLongestCurrentStreak", Integer.toString(stats.pushupStreaks().longestCurrentStreak()));
        values.put("pushupLongestStreak", Integer.toString(stats.pushupStreaks().longestStreak()));
        values.put("routineTotalEntries", Long.toString(stats.routineCompletion().totalEntries()));
        values.put("routineCompletedEntries", Long.toString(stats.routineCompletion().completedEntries()));
        values.put("routineCompletionRate", Double.toString(stats.routineCompletion().completionRate()));
        values.put("retentionEligibleUsers", Long.toString(stats.userRetention().eligibleUsers()));
        values.put("retentionRetainedUsers", Long.toString(stats.userRetention().retainedUsers()));
        values.put("retentionRate", Double.toString(stats.userRetention().retentionRate()));
        values.put("generatedAt", stats.generatedAt().toString());

        List<List<String>> rows = new ArrayList<>();
        rows.add(List.of("metric", "value"));
        values.forEach((metric, value) -> rows.add(List.of(metric, value)));
        return rows;
    }

    private String csv(List<List<String>> rows) {
        StringBuilder builder = new StringBuilder();
        for (List<String> row : rows) {
            for (int i = 0; i < row.size(); i++) {
                if (i > 0) {
                    builder.append(',');
                }
                builder.append(csvValue(row.get(i)));
            }
            builder.append('\n');
        }
        return builder.toString();
    }

    private String csvValue(String value) {
        String safeValue = Objects.toString(value, "");
        if (safeValue.contains(",") || safeValue.contains("\"") || safeValue.contains("\n")) {
            return "\"" + safeValue.replace("\"", "\"\"") + "\"";
        }
        return safeValue;
    }

    private byte[] xlsx(List<List<String>> rows) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (ZipOutputStream zip = new ZipOutputStream(bytes)) {
                writeZipEntry(zip, "[Content_Types].xml", contentTypesXml());
                writeZipEntry(zip, "_rels/.rels", rootRelationshipsXml());
                writeZipEntry(zip, "xl/workbook.xml", workbookXml());
                writeZipEntry(zip, "xl/_rels/workbook.xml.rels", workbookRelationshipsXml());
                writeZipEntry(zip, "xl/worksheets/sheet1.xml", worksheetXml(rows));
            }
            return bytes.toByteArray();
        } catch (IOException ex) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not generate Excel export");
        }
    }

    private void writeZipEntry(ZipOutputStream zip, String name, String content) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(content.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private String contentTypesXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
            + "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>"
            + "<Default Extension=\"xml\" ContentType=\"application/xml\"/>"
            + "<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>"
            + "<Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>"
            + "</Types>";
    }

    private String rootRelationshipsXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
            + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>"
            + "</Relationships>";
    }

    private String workbookXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">"
            + "<sheets><sheet name=\"Export\" sheetId=\"1\" r:id=\"rId1\"/></sheets>"
            + "</workbook>";
    }

    private String workbookRelationshipsXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
            + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>"
            + "</Relationships>";
    }

    private String worksheetXml(List<List<String>> rows) {
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        builder.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><sheetData>");
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            builder.append("<row r=\"").append(rowIndex + 1).append("\">");
            List<String> row = rows.get(rowIndex);
            for (int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                builder.append("<c r=\"").append(columnName(columnIndex + 1)).append(rowIndex + 1).append("\" t=\"inlineStr\"><is><t>");
                builder.append(xmlEscape(row.get(columnIndex)));
                builder.append("</t></is></c>");
            }
            builder.append("</row>");
        }
        builder.append("</sheetData></worksheet>");
        return builder.toString();
    }

    private String columnName(int index) {
        StringBuilder builder = new StringBuilder();
        int value = index;
        while (value > 0) {
            int remainder = (value - 1) % 26;
            builder.insert(0, (char) ('A' + remainder));
            value = (value - 1) / 26;
        }
        return builder.toString();
    }

    private String xmlEscape(String value) {
        return Objects.toString(value, "")
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;");
    }
}
