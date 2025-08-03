package com.mriridescent.threatdetection.model;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Represents a user's behavioral profile for anomaly detection.
 */
@Data
@Builder
public class UserBehaviorProfile {
    
    private String userId;
    private Duration averageSessionDuration;
    private Set<Integer> typicalAccessHours;
    private Set<String> frequentResources;
    private String riskProfile;
    private LocalDateTime lastAnalyzed;
    private int totalSessions;
    private Set<String> typicalIpAddresses;
    private Set<String> typicalUserAgents;
    
    /**
     * Check if an access hour is typical for this user.
     */
    public boolean isTypicalAccessHour(int hour) {
        return typicalAccessHours != null && typicalAccessHours.contains(hour);
    }
    
    /**
     * Check if a resource is frequently accessed by this user.
     */
    public boolean isFrequentResource(String resource) {
        return frequentResources != null && frequentResources.contains(resource);
    }
    
    /**
     * Check if an IP address is typical for this user.
     */
    public boolean isTypicalIpAddress(String ipAddress) {
        return typicalIpAddresses != null && typicalIpAddresses.contains(ipAddress);
    }
    
    /**
     * Get the risk level of this user profile.
     */
    public String getRiskLevel() {
        return riskProfile != null ? riskProfile : "UNKNOWN";
    }
    
    /**
     * Check if the profile needs to be updated.
     */
    public boolean needsUpdate() {
        if (lastAnalyzed == null) {
            return true;
        }
        return Duration.between(lastAnalyzed, LocalDateTime.now()).toDays() > 7;
    }
}
