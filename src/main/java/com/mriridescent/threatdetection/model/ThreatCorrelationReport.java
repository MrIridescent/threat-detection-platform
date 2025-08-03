package com.mriridescent.threatdetection.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Comprehensive report of threat correlation analysis.
 */
@Data
public class ThreatCorrelationReport {
    
    private String workflowId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<ThreatAlert> networkAlerts;
    private List<ThreatAlert> behaviorAlerts;
    private List<ThreatCorrelation> correlations;
    private double aggregatedRiskScore;
    private List<ResponseAction> responseActions;
    
    /**
     * Get the total number of alerts analyzed.
     */
    public int getTotalAlertsAnalyzed() {
        int networkCount = networkAlerts != null ? networkAlerts.size() : 0;
        int behaviorCount = behaviorAlerts != null ? behaviorAlerts.size() : 0;
        return networkCount + behaviorCount;
    }
    
    /**
     * Get the number of correlations found.
     */
    public int getCorrelationCount() {
        return correlations != null ? correlations.size() : 0;
    }
    
    /**
     * Get the number of high-confidence correlations.
     */
    public long getHighConfidenceCorrelations() {
        if (correlations == null) {
            return 0;
        }
        return correlations.stream()
                .filter(ThreatCorrelation::isHighConfidence)
                .count();
    }
    
    /**
     * Get the aggregated risk level.
     */
    public String getAggregatedRiskLevel() {
        if (aggregatedRiskScore >= 0.8) {
            return "CRITICAL";
        } else if (aggregatedRiskScore >= 0.6) {
            return "HIGH";
        } else if (aggregatedRiskScore >= 0.4) {
            return "MEDIUM";
        } else if (aggregatedRiskScore >= 0.2) {
            return "LOW";
        } else {
            return "MINIMAL";
        }
    }
    
    /**
     * Check if immediate action is required.
     */
    public boolean requiresImmediateAction() {
        return aggregatedRiskScore >= 0.8 || getHighConfidenceCorrelations() >= 3;
    }
    
    /**
     * Get the number of automated response actions.
     */
    public long getAutomatedActionCount() {
        if (responseActions == null) {
            return 0;
        }
        return responseActions.stream()
                .filter(ResponseAction::isAutomated)
                .count();
    }
    
    /**
     * Get the number of manual response actions.
     */
    public long getManualActionCount() {
        if (responseActions == null) {
            return 0;
        }
        return responseActions.stream()
                .filter(action -> !action.isAutomated())
                .count();
    }
}
