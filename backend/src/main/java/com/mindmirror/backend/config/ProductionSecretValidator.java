package com.mindmirror.backend.config;

import java.util.Arrays;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.mindmirror.backend.security.JwtProperties;

@Component
public class ProductionSecretValidator implements ApplicationRunner {

    private static final String DEVELOPMENT_JWT_SECRET = "change-this-development-secret-at-least-32-bytes";
    private static final String DEVELOPMENT_DB_PASSWORD = "change_me";

    private final Environment environment;
    private final JwtProperties jwtProperties;

    public ProductionSecretValidator(Environment environment, JwtProperties jwtProperties) {
        this.environment = environment;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!isProductionProfile()) {
            return;
        }

        String jwtSecret = jwtProperties.secret();
        if (jwtSecret == null || jwtSecret.length() < 32 || DEVELOPMENT_JWT_SECRET.equals(jwtSecret)) {
            throw new IllegalStateException("Production profile requires JWT_SECRET with at least 32 non-default characters.");
        }

        String dbPassword = environment.getProperty("spring.datasource.password");
        if (dbPassword == null || dbPassword.isBlank() || DEVELOPMENT_DB_PASSWORD.equals(dbPassword)) {
            throw new IllegalStateException("Production profile requires DB_PASSWORD to be set to a non-default value.");
        }
    }

    private boolean isProductionProfile() {
        return Arrays.stream(environment.getActiveProfiles()).anyMatch("prod"::equalsIgnoreCase);
    }
}
