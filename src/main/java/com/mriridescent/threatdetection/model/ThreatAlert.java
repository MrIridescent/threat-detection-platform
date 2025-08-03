package com.mriridescent.threatdetection.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a detected security threat with enhanced metadata.
 */
@Data
public class ThreatAlert {
    private String alertId = UUID.randomUUID().toString();
    private String sourceIp;
    private String destinationIp;
    private String userId;
    private String systemId;
    private String description;
    private Severity severity;
    private LocalDateTime detectionTime;
    private double confidenceScore;
    private String detectedBy;
    private String threatType;

    // Enhanced fields for coordinator integration
    private ThreatIntelligence intelligenceData;
    private RiskScore riskScore;
    private String correlationId;
    private String workflowId;
    private boolean processed = false;
    private LocalDateTime processedAt;

    public enum Severity {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    /**
     * Mark the alert as processed.
     */
    public void markProcessed(String workflowId) {
        this.processed = true;
        this.processedAt = LocalDateTime.now();
        this.workflowId = workflowId;
    }

    /**
     * Check if the alert is high severity (HIGH or CRITICAL).
     */
    public boolean isHighSeverity() {
        return severity == Severity.HIGH || severity == Severity.CRITICAL;
    }

    /**
     * Check if the alert is critical.
     */
    public boolean isCritical() {
        return severity == Severity.CRITICAL;
    }

    /**
     * Get severity level as numeric value.
     */
    public int getSeverityLevel() {
        return severity != null ? severity.ordinal() : 0;
    }

    /**
     * Check if intelligence data is available.
     */
    public boolean hasIntelligenceData() {
        return intelligenceData != null;
    }

    /**
     * Check if risk score is available.
     */
    public boolean hasRiskScore() {
        return riskScore != null;
    }
}
