package com.mriridescent.threatdetection.agent.core;

import java.util.concurrent.CompletableFuture;

/**
 * Core interface for all AI agents in the system.
 * Defines the basic operations that every agent must implement.
 */
public interface AgentFramework {

    /**
     * Initializes the agent with necessary configuration.
     */
    void initialize();

    /**
     * Starts agent processing.
     */
    void start();

    /**
     * Stops agent processing.
     */
    void stop();

    /**
     * Returns the current status of the agent.
     * @return Status information
     */
    AgentStatus getStatus();

    /**
     * Submits a task to the agent for asynchronous processing.
     * @param task Task to process
     * @return Future result of task processing
     */
    <T, R> CompletableFuture<R> submitTask(AgentTask<T, R> task);
}
