package com.mriridescent.iris;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main application class for Project Iris - AI-Powered Threat Detection Engine.
 * This system uses machine learning to analyze email metadata and content
 * for anomalous patterns indicative of sophisticated, zero-day phishing attacks.
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableCaching
public class IrisApplication {

    public static void main(String[] args) {
        SpringApplication.run(IrisApplication.class, args);
    }
}
