package com.mriridescent.threatdetection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main application class for the Threat Detection Platform.
 * This is the entry point for the advanced AI-powered threat detection ecosystem.
 * The system integrates multiple specialized AI agents working together to detect and respond to threats.
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableCaching
public class ThreatDetectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThreatDetectionApplication.class, args);
    }
}
