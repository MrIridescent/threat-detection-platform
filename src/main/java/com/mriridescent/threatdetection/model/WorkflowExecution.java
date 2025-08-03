package com.mriridescent.threatdetection.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents the execution state of a workflow.
 * Tracks the progress and status of workflow execution.
 */
@Data
@Builder
public class WorkflowExecution {
    
    private String workflowId;
    private String workflowName;
    private WorkflowStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Object input;
    private List<WorkflowStep> steps;
    private String errorMessage;
    private String result;
    
    /**
     * Add a new step to the workflow execution.
     */
    public void addStep(String stepName, String description) {
        WorkflowStep step = WorkflowStep.builder()
                .stepName(stepName)
                .description(description)
                .timestamp(LocalDateTime.now())
                .status(WorkflowStepStatus.COMPLETED)
                .build();
        steps.add(step);
    }
    
    /**
     * Mark the workflow as completed successfully.
     */
    public void complete(String result) {
        this.status = WorkflowStatus.COMPLETED;
        this.endTime = LocalDateTime.now();
        this.result = result;
    }
    
    /**
     * Mark the workflow as failed.
     */
    public void fail(String errorMessage) {
        this.status = WorkflowStatus.FAILED;
        this.endTime = LocalDateTime.now();
        this.errorMessage = errorMessage;
    }
    
    /**
     * Check if the workflow is completed (either successfully or with failure).
     */
    public boolean isCompleted() {
        return status == WorkflowStatus.COMPLETED || status == WorkflowStatus.FAILED;
    }
    
    /**
     * Get the current step count.
     */
    public int getStepCount() {
        return steps != null ? steps.size() : 0;
    }
}

/**
 * Workflow execution status enumeration.
 */
enum WorkflowStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELLED
}

/**
 * Individual workflow step.
 */
@Data
@Builder
class WorkflowStep {
    private String stepName;
    private String description;
    private LocalDateTime timestamp;
    private WorkflowStepStatus status;
    private String errorMessage;
}

/**
 * Workflow step status enumeration.
 */
enum WorkflowStepStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
    SKIPPED
}
