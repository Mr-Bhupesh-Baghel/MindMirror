package com.mindmirror.backend.security;

import java.io.IOException;
import java.time.Clock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mindmirror.backend.config.RateLimitProperties;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final long WINDOW_MILLIS = 60_000L;

    private final RateLimitProperties properties;
    private final Clock clock;
    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    @Autowired
    public RateLimitingFilter(RateLimitProperties properties) {
        this(properties, Clock.systemUTC());
    }

    RateLimitingFilter(RateLimitProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !properties.enabled()
            || properties.requestsPerMinute() <= 0
            || "OPTIONS".equalsIgnoreCase(request.getMethod())
            || path.equals("/api/health")
            || path.equals("/actuator/health")
            || path.startsWith("/actuator/health/")
            || path.equals("/actuator/info");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String key = clientKey(request);
        long now = clock.millis();
        Window window = windows.compute(key, (ignored, existing) -> nextWindow(existing, now));

        response.setHeader("X-RateLimit-Limit", String.valueOf(properties.requestsPerMinute()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, properties.requestsPerMinute() - window.count())));

        if (window.count() > properties.requestsPerMinute()) {
            response.setStatus(429);
            response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(Math.max(1, (window.resetAtMillis() - now) / 1000)));
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\":\"Too Many Requests\",\"message\":\"Rate limit exceeded\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Window nextWindow(Window existing, long now) {
        if (existing == null || now >= existing.resetAtMillis()) {
            return new Window(1, now + WINDOW_MILLIS);
        }
        return new Window(existing.count() + 1, existing.resetAtMillis());
    }

    private String clientKey(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",", 2)[0].trim();
        }
        return request.getRemoteAddr();
    }

    private record Window(int count, long resetAtMillis) {
    }
}
