package com.mriridescent.threatdetection.orchestration;

import com.mriridescent.threatdetection.agent.core.AgentFramework;
import com.mriridescent.threatdetection.agent.core.AgentStatus;
import com.mriridescent.threatdetection.agent.core.AgentTask;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Orchestrates communication and coordination between different AI agents.
 */
@Slf4j
@Component
public class AgentOrchestrator {

    private final Map<String, AgentFramework> agents = new HashMap<>();

    /**
     * Construct with all available agents in the system.
     */
    public AgentOrchestrator(List<AgentFramework> agentList) {
        agentList.forEach(agent -> agents.put(agent.getAgentId(), agent));
        log.info("Agent Orchestrator initialized with {} agents", agents.size());
    }

    /**
     * Initialize all agents after construction.
     */
    @PostConstruct
    public void initializeAgents() {
        agents.values().forEach(AgentFramework::initialize);
        log.info("All agents initialized");
    }

    /**
     * Start all agents.
     */
    public void startAllAgents() {
        agents.values().forEach(AgentFramework::start);
        log.info("All agents started");
    }

    /**
     * Stop all agents.
     */
    public void stopAllAgents() {
        agents.values().forEach(AgentFramework::stop);
        log.info("All agents stopped");
    }

    /**
     * Get the status of all agents.
     */
    public Map<String, AgentStatus> getAllAgentStatus() {
        Map<String, AgentStatus> statuses = new HashMap<>();
        agents.forEach((id, agent) -> statuses.put(id, agent.getStatus()));
        return statuses;
    }

    /**
     * Submit a task to a specific agent.
     */
    public <T, R> CompletableFuture<R> submitTask(String agentId, T input) {
        AgentFramework agent = agents.get(agentId);
        if (agent == null) {
            throw new IllegalArgumentException("Unknown agent ID: " + agentId);
        }

        AgentTask<T, R> task = new AgentTask<>();
        task.setInput(input);

        log.debug("Submitting task to agent: {}", agentId);
        return agent.submitTask(task);
    }

    /**
     * Route a task result to another agent for further processing.
     */
    public <T, R, U> CompletableFuture<U> routeTaskResult(
            CompletableFuture<R> resultFuture, String targetAgentId) {

        return resultFuture.thenCompose(result -> {
            if (result == null) {
                return CompletableFuture.completedFuture(null);
            }

            log.debug("Routing result to agent: {}", targetAgentId);
            return this.<R, U>submitTask(targetAgentId, result);
        });
    }
}
