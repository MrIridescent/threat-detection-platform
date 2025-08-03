package com.mriridescent.threatdetection.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Represents threat intelligence information about an indicator.
 * Enhanced with additional metadata and analysis capabilities.
 */
@Data
@Builder
public class ThreatIntelligence {
    private String indicator;
    private IntelligenceQuery.IndicatorType indicatorType;
    private boolean isMalicious;
    private double confidenceScore;
    private double confidence; // Alternative field name for compatibility
    private String threatType;
    private String source;
    private List<String> sources;
    private LocalDateTime timestamp;
    private LocalDateTime lastUpdated;
    private LocalDateTime firstSeen;
    private LocalDateTime lastSeen;
    private String additionalDetails;
    private String description;
    private String reputation;
    private String category;
    private String malwareFamily;
    private List<String> tags;
    private Map<String, Object> attributes;
    private boolean isActive;

    /**
     * Get confidence score (supports both field names).
     */
    public double getConfidence() {
        return confidence != 0 ? confidence : confidenceScore;
    }

    /**
     * Set confidence score (updates both fields for compatibility).
     */
    public void setConfidence(double confidence) {
        this.confidence = confidence;
        this.confidenceScore = confidence;
    }

    /**
     * Check if this is high-confidence intelligence.
     */
    public boolean isHighConfidence() {
        return getConfidence() >= 0.8;
    }

    /**
     * Check if this is medium-confidence intelligence.
     */
    public boolean isMediumConfidence() {
        double conf = getConfidence();
        return conf >= 0.5 && conf < 0.8;
    }

    /**
     * Check if this is low-confidence intelligence.
     */
    public boolean isLowConfidence() {
        return getConfidence() < 0.5;
    }

    /**
     * Check if the indicator is suspicious.
     */
    public boolean isSuspicious() {
        return "SUSPICIOUS".equalsIgnoreCase(reputation) ||
               "QUESTIONABLE".equalsIgnoreCase(reputation);
    }

    /**
     * Check if the indicator is clean/benign.
     */
    public boolean isClean() {
        return "CLEAN".equalsIgnoreCase(reputation) ||
               "BENIGN".equalsIgnoreCase(reputation) ||
               "GOOD".equalsIgnoreCase(reputation);
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
     * Check if the intelligence data is recent (within last 24 hours).
     */
    public boolean isRecent() {
        LocalDateTime updateTime = lastUpdated != null ? lastUpdated : timestamp;
        if (updateTime == null) {
            return false;
        }
        return updateTime.isAfter(LocalDateTime.now().minusDays(1));
    }

    /**
     * Get a summary of the threat intelligence.
     */
    public String getSummary() {
        return String.format("%s (%s): %s - Confidence: %.2f (%s)",
                indicator,
                indicatorType,
                reputation != null ? reputation : (isMalicious ? "MALICIOUS" : "UNKNOWN"),
                getConfidence(),
                getConfidenceLevel());
    }
}
