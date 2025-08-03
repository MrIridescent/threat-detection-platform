package com.mriridescent.phishnet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main application class for PhishNet Analyst - Phishing Campaign Visualization Tool.
 * This tool maps out the infrastructure and logical flow of complex phishing campaigns,
 * aiding in strategic takedowns and defensive posture planning.
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableCaching
public class PhishNetApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhishNetApplication.class, args);
    }
}
