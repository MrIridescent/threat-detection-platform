package com.mriridescent.threatdetection.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Represents a calculated risk score with component breakdown.
 */
@Data
@Builder
public class RiskScore {
    
    private double score;
    private double baseRisk;
    private double timeRisk;
    private double locationRisk;
    private double behaviorRisk;
    private LocalDateTime calculatedAt;
    
    /**
     * Get risk level as a string.
     */
    public String getRiskLevel() {
        if (score >= 0.8) {
            return "CRITICAL";
        } else if (score >= 0.6) {
            return "HIGH";
        } else if (score >= 0.4) {
            return "MEDIUM";
        } else if (score >= 0.2) {
            return "LOW";
        } else {
            return "MINIMAL";
        }
    }
    
    /**
     * Check if the risk score is above a threshold.
     */
    public boolean isAboveThreshold(double threshold) {
        return score > threshold;
    }
    
    /**
     * Get the dominant risk factor.
     */
    public String getDominantRiskFactor() {
        double max = Math.max(Math.max(baseRisk, timeRisk), Math.max(locationRisk, behaviorRisk));
        
        if (max == baseRisk) {
            return "BASE_THREAT";
        } else if (max == timeRisk) {
            return "TIME_BASED";
        } else if (max == locationRisk) {
            return "LOCATION_BASED";
        } else {
            return "BEHAVIOR_BASED";
        }
    }
}
