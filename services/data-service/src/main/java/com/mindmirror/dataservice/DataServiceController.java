package com.mindmirror.dataservice;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/data")
public class DataServiceController {
    private static final String GUEST_BACKUPS_KEY = "__mindmirrorGuestBackups";

    private final UserDataRepository dataRepository;
    private final ObjectMapper objectMapper;
    private final TokenService tokenService;

    public DataServiceController(UserDataRepository dataRepository, ObjectMapper objectMapper, TokenService tokenService) {
        this.dataRepository = dataRepository;
        this.objectMapper = objectMapper;
        this.tokenService = tokenService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public DataResponse getData(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String userId = tokenService.requireUserId(authorization);
        return new DataResponse(readData(userId), Instant.now().toString());
    }

    @PutMapping
    @Transactional
    public DataResponse replaceData(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody Map<String, Object> payload) {
        String userId = tokenService.requireUserId(authorization);
        Map<String, Object> data = new LinkedHashMap<>(payload == null ? Map.of() : payload);
        saveData(userId, data);
        return new DataResponse(data, Instant.now().toString());
    }

    @PostMapping("/migrate")
    @Transactional
    public MigrationResponse migrate(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody MigrationRequest request) {
        String userId = tokenService.requireUserId(authorization);
        Map<String, Object> current = readData(userId);
        Map<String, Object> guestData = request.guestData() == null ? Map.of() : request.guestData();
        List<String> conflicts = new ArrayList<>();

        guestData.forEach((key, guestValue) -> {
            if (!current.containsKey(key)) {
                current.put(key, guestValue);
            } else {
                conflicts.add(key);
                current.put(key, mergeValues(current.get(key), guestValue));
            }
        });

        List<Map<String, Object>> backups = new ArrayList<>();
        Object existingBackups = current.get(GUEST_BACKUPS_KEY);
        if (existingBackups instanceof List<?> list) {
            list.forEach(item -> {
                if (item instanceof Map<?, ?> map) {
                    Map<String, Object> backup = new LinkedHashMap<>();
                    map.forEach((key, value) -> backup.put(String.valueOf(key), value));
                    backups.add(backup);
                }
            });
        }
        backups.add(Map.of("migratedAt", Instant.now().toString(), "data", guestData));
        current.put(GUEST_BACKUPS_KEY, backups);
        saveData(userId, current);

        return new MigrationResponse(true, guestData.keySet().stream().toList(), conflicts, Instant.now().toString());
    }

    private Map<String, Object> readData(String userId) {
        return dataRepository.findById(userId)
                .map(UserData::getDataJson)
                .map(this::parseData)
                .orElseGet(LinkedHashMap::new);
    }

    private void saveData(String userId, Map<String, Object> data) {
        try {
            String json = objectMapper.writeValueAsString(data);
            UserData entity = dataRepository.findById(userId)
                    .orElseGet(() -> new UserData(userId, json));
            entity.setDataJson(json);
            dataRepository.save(entity);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not save account data", exception);
        }
    }

    private Map<String, Object> parseData(String json) {
        try {
            return new LinkedHashMap<>(objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            }));
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Account data is not readable", exception);
        }
    }

    private Object mergeValues(Object accountValue, Object guestValue) {
        if (accountValue instanceof Map<?, ?> accountMap && guestValue instanceof Map<?, ?> guestMap) {
            Map<String, Object> merged = new LinkedHashMap<>();
            accountMap.forEach((key, value) -> merged.put(String.valueOf(key), value));
            guestMap.forEach((key, value) -> merged.putIfAbsent(String.valueOf(key), value));
            return merged;
        }
        if (accountValue instanceof List<?> accountList && guestValue instanceof List<?> guestList) {
            List<Object> merged = new ArrayList<>(accountList);
            guestList.forEach(value -> {
                if (!merged.contains(value)) {
                    merged.add(value);
                }
            });
            return merged;
        }
        return accountValue;
    }

    public record DataResponse(Map<String, Object> data, String savedAt) {
    }

    public record MigrationRequest(@NotNull Map<String, Object> guestData) {
    }

    public record MigrationResponse(boolean saved, List<String> migratedKeys, List<String> conflicts, String savedAt) {
    }
}
