package com.mriridescent.threatdetection.model;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;

/**
 * Represents the result of a workflow execution.
 * Contains both the result data and metadata about the execution.
 */
@Data
@Builder
public class WorkflowResult<T> {
    
    private String workflowId;
    private boolean success;
    private T result;
    private String error;
    private Duration executionTime;
    private ThreatAlert threatAlert;
    private RiskScore riskScore;
    
    /**
     * Check if the workflow completed successfully.
     */
    public boolean isSuccessful() {
        return success && error == null;
    }
    
    /**
     * Check if the workflow failed.
     */
    public boolean isFailed() {
        return !success || error != null;
    }
    
    /**
     * Get execution time in milliseconds.
     */
    public long getExecutionTimeMillis() {
        return executionTime != null ? executionTime.toMillis() : 0;
    }
}
