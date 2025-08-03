package com.mriridescent.threatdetection.agent.core;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Represents the current status of an AI agent.
 */
@Data
public class AgentStatus {
    private String agentId;
    private AgentState state;
    private LocalDateTime lastActivity;
    private int pendingTasks;
    private int completedTasks;
    private String additionalInfo;

    public enum AgentState {
        INITIALIZING,
        READY,
        PROCESSING,
        WAITING,
        ERROR,
        STOPPED
    }
}
