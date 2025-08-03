package com.mriridescent.threatdetection.agent.core;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * Abstract base implementation of the AgentFramework.
 * Provides common functionality for all AI agents.
 */
@Slf4j
public abstract class AbstractAgent implements AgentFramework {

    @Getter
    protected final String agentId;
    protected final ThreadPoolTaskExecutor executor;
    protected final AgentStatus status;
    protected boolean initialized = false;

    public AbstractAgent(String agentId, ThreadPoolTaskExecutor executor) {
        this.agentId = agentId;
        this.executor = executor;
        this.status = new AgentStatus();
        this.status.setAgentId(agentId);
        this.status.setState(AgentStatus.AgentState.INITIALIZING);
        this.status.setLastActivity(LocalDateTime.now());
    }

    @Override
    public void initialize() {
        log.info("Initializing agent: {}", agentId);
        // Specific initialization logic to be implemented by subclasses
        initialized = true;
        status.setState(AgentStatus.AgentState.READY);
        status.setLastActivity(LocalDateTime.now());
    }

    @Override
    public void start() {
        if (!initialized) {
            initialize();
        }
        log.info("Starting agent: {}", agentId);
        status.setState(AgentStatus.AgentState.PROCESSING);
        status.setLastActivity(LocalDateTime.now());
    }

    @Override
    public void stop() {
        log.info("Stopping agent: {}", agentId);
        status.setState(AgentStatus.AgentState.STOPPED);
        status.setLastActivity(LocalDateTime.now());
    }

    @Override
    public AgentStatus getStatus() {
        return status;
    }

    @Override
    public <T, R> CompletableFuture<R> submitTask(AgentTask<T, R> task) {
        log.debug("Agent {} received task: {}", agentId, task.getTaskId());
        task.setTargetAgentId(agentId);
        status.setPendingTasks(status.getPendingTasks() + 1);
        status.setLastActivity(LocalDateTime.now());

        return CompletableFuture.supplyAsync(() -> {
            try {
                status.setState(AgentStatus.AgentState.PROCESSING);
                R result = processTask(task);
                status.setPendingTasks(status.getPendingTasks() - 1);
                status.setCompletedTasks(status.getCompletedTasks() + 1);
                if (status.getPendingTasks() == 0) {
                    status.setState(AgentStatus.AgentState.READY);
                }
                return result;
            } catch (Exception e) {
                log.error("Error processing task: {}", task.getTaskId(), e);
                status.setState(AgentStatus.AgentState.ERROR);
                status.setAdditionalInfo("Error: " + e.getMessage());
                throw new RuntimeException("Agent processing error", e);
            } finally {
                status.setLastActivity(LocalDateTime.now());
            }
        }, executor);
    }

    /**
     * Process the given task and return a result.
     * This method must be implemented by all agent subclasses.
     * 
     * @param task The task to process
     * @return The result of task processing
     */
    protected abstract <T, R> R processTask(AgentTask<T, R> task);
}
