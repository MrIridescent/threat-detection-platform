package com.mriridescent.threatdetection.agent.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a task that can be assigned to an AI agent.
 * @param <T> Type of input data
 * @param <R> Type of result data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentTask<T, R> {
    private String taskId = UUID.randomUUID().toString();
    private T input;
    private LocalDateTime createdAt = LocalDateTime.now();
    private int priority = 5;
    private String sourceAgentId;
    private String targetAgentId;
}
