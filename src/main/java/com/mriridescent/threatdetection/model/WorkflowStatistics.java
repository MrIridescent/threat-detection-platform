package com.mriridescent.threatdetection.model;

import lombok.Builder;
import lombok.Data;

/**
 * Statistics about workflow execution.
 */
@Data
@Builder
public class WorkflowStatistics {
    
    private int activeWorkflows;
    private long totalStarted;
    private long totalCompleted;
    private long totalFailed;
    private double averageDuration;
    
    /**
     * Calculate success rate as a percentage.
     */
    public double getSuccessRate() {
        if (totalStarted == 0) {
            return 0.0;
        }
        return (double) totalCompleted / totalStarted * 100.0;
    }
    
    /**
     * Calculate failure rate as a percentage.
     */
    public double getFailureRate() {
        if (totalStarted == 0) {
            return 0.0;
        }
        return (double) totalFailed / totalStarted * 100.0;
    }
    
    /**
     * Get total workflows processed.
     */
    public long getTotalProcessed() {
        return totalCompleted + totalFailed;
    }
}
