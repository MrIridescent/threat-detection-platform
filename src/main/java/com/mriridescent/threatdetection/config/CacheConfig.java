package com.mriridescent.threatdetection.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration for application caching.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * In-memory cache manager for development environment
     *
     * @return CacheManager instance
     */
    @Bean
    @Profile({"default", "dev", "test"})
    public CacheManager cacheManagerDev() {
        return new ConcurrentMapCacheManager(
                // ML Model caches
                "mlModels", "activeModels", "mlModel",
                // Phishing Campaign caches
                "phishingCampaigns", "activeCampaigns", "phishingCampaign",
                // Attack Vector caches
                "attackVectors", "campaignAttackVectors", "attackVector",
                // Infrastructure Node caches
                "infrastructureNodes", "campaignInfrastructureNodes", "infrastructureNode"
        );
    }
}
