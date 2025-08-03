package com.mriridescent.threatdetection.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration for API rate limiting to prevent abuse and DoS attacks.
 * Implements different rate limits for different types of endpoints.
 */
@Configuration
@RequiredArgsConstructor
public class RateLimitingConfig {

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        return RateLimiterRegistry.of(RateLimiterConfig.ofDefaults());
    }

    @Bean(name = "standardApiLimiter")
    public RateLimiter standardApiLimiter(RateLimiterRegistry rateLimiterRegistry) {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .limitForPeriod(20)
                .timeoutDuration(Duration.ofMillis(100))
                .build();

        return rateLimiterRegistry.rateLimiter("standardApi", config);
    }

    @Bean(name = "authLimiter")
    public RateLimiter authLimiter(RateLimiterRegistry rateLimiterRegistry) {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .limitForPeriod(10) // More restrictive for auth endpoints
                .timeoutDuration(Duration.ofMillis(100))
                .build();

        return rateLimiterRegistry.rateLimiter("auth", config);
    }

    @Bean(name = "irisAnalysisLimiter")
    public RateLimiter irisAnalysisLimiter(RateLimiterRegistry rateLimiterRegistry) {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(10))
                .limitForPeriod(5) // Resource-intensive operations get stricter limits
                .timeoutDuration(Duration.ofMillis(200))
                .build();

        return rateLimiterRegistry.rateLimiter("irisAnalysis", config);
    }
}
