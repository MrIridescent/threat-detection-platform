package com.mriridescent.threatdetection.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Represents a correlation between different threat alerts.
 */
@Data
@Builder
public class ThreatCorrelation {
    
    private String correlationType;
    private ThreatAlert networkAlert;
    private ThreatAlert behaviorAlert;
    private double confidence;
    private String description;
    private LocalDateTime correlatedAt;
    
    /**
     * Check if this is a high-confidence correlation.
     */
    public boolean isHighConfidence() {
        return confidence >= 0.8;
    }
    
    /**
     * Check if this is a medium-confidence correlation.
     */
    public boolean isMediumConfidence() {
        return confidence >= 0.5 && confidence < 0.8;
    }
    
    /**
     * Check if this is a low-confidence correlation.
     */
    public boolean isLowConfidence() {
        return confidence < 0.5;
    }
    
    /**
     * Get the confidence level as a string.
     */
    public String getConfidenceLevel() {
        if (isHighConfidence()) {
            return "HIGH";
        } else if (isMediumConfidence()) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }
    
    /**
     * Get a summary of the correlation.
     */
    public String getSummary() {
        return String.format("%s correlation between %s and %s (confidence: %.2f)", 
                correlationType, 
                networkAlert != null ? networkAlert.getDescription() : "unknown",
                behaviorAlert != null ? behaviorAlert.getDescription() : "unknown",
                confidence);
    }
}
