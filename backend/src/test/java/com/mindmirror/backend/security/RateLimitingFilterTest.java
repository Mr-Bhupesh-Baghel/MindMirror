package com.mindmirror.backend.security;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.mindmirror.backend.config.RateLimitProperties;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitingFilterTest {

    @Test
    void rejectsRequestsAfterConfiguredWindowLimit() throws Exception {
        RateLimitingFilter filter = new RateLimitingFilter(
            new RateLimitProperties(true, 2),
            Clock.fixed(Instant.parse("2026-07-04T00:00:00Z"), ZoneOffset.UTC)
        );

        MockHttpServletResponse first = perform(filter);
        MockHttpServletResponse second = perform(filter);
        MockHttpServletResponse third = perform(filter);

        assertThat(first.getStatus()).isEqualTo(200);
        assertThat(second.getStatus()).isEqualTo(200);
        assertThat(third.getStatus()).isEqualTo(429);
        assertThat(third.getHeader("Retry-After")).isNotBlank();
    }

    private MockHttpServletResponse perform(RateLimitingFilter filter) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/water");
        request.setRemoteAddr("192.0.2.10");
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        return response;
    }
}
