package com.mriridescent.threatdetection.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a response action to be taken for a threat.
 * Enhanced with priority, automation flags, and execution tracking.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseAction {
    private String actionType;
    private String target;
    private boolean automatic;
    private boolean automated; // Alternative field name used in coordinator
    private String description;
    private String status;
    private int priority;
    private LocalDateTime createdAt;
    private LocalDateTime executedAt;
    private String executedBy;
    private String result;
    private String errorMessage;

    public ResponseAction(String actionType, String target, boolean automatic) {
        this.actionType = actionType;
        this.target = target;
        this.automatic = automatic;
        this.automated = automatic;
        this.status = "PENDING";
        this.priority = 5; // Default priority
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Check if this action is automated (supports both field names).
     */
    public boolean isAutomated() {
        return automated || automatic;
    }

    /**
     * Set automated status (updates both fields for compatibility).
     */
    public void setAutomated(boolean automated) {
        this.automated = automated;
        this.automatic = automated;
    }

    /**
     * Mark the action as executed.
     */
    public void markExecuted(String executedBy, String result) {
        this.status = "EXECUTED";
        this.executedAt = LocalDateTime.now();
        this.executedBy = executedBy;
        this.result = result;
    }

    /**
     * Mark the action as failed.
     */
    public void markFailed(String errorMessage) {
        this.status = "FAILED";
        this.executedAt = LocalDateTime.now();
        this.errorMessage = errorMessage;
    }

    /**
     * Check if the action is pending execution.
     */
    public boolean isPending() {
        return "PENDING".equals(status);
    }

    /**
     * Check if the action was executed successfully.
     */
    public boolean isExecuted() {
        return "EXECUTED".equals(status);
    }

    /**
     * Check if the action failed.
     */
    public boolean isFailed() {
        return "FAILED".equals(status);
    }

    /**
     * Get priority level as string.
     */
    public String getPriorityLevel() {
        if (priority >= 9) {
            return "CRITICAL";
        } else if (priority >= 7) {
            return "HIGH";
        } else if (priority >= 5) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }
}
