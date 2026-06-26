package com.mindmirror.backend.health;

import java.time.Instant;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    public HealthController(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @GetMapping("/health")
    public HealthResponse health() {
        Integer databaseResult = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        return new HealthResponse("UP", databaseResult != null && databaseResult == 1, Instant.now());
    }
}
