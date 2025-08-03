package com.mriridescent.threatdetection.agent.coordinator;

import com.mriridescent.threatdetection.model.*;
import com.mriridescent.threatdetection.orchestration.AgentOrchestrator;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Enhanced Agent Coordinator that orchestrates complex workflows between multiple AI agents.
 * Provides enterprise-grade features including resilience patterns, monitoring, and workflow management.
 *
 * Key Features:
 * - Circuit breakers for fault tolerance
 * - Retry mechanisms for transient failures
 * - Rate limiting for resource protection
 * - Comprehensive metrics and monitoring
 * - Workflow state management
 * - Conditional workflow execution
 * - Performance optimization with caching
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AgentCoordinator {

    private final AgentOrchestrator orchestrator;
    private final MeterRegistry meterRegistry;

    // Configuration properties
    @Value("${threat-detection.coordinator.max-concurrent-workflows:50}")
    private int maxConcurrentWorkflows;

    @Value("${threat-detection.coordinator.workflow-timeout:300}")
    private int workflowTimeoutSeconds;

    @Value("${threat-detection.coordinator.enable-intelligence-enrichment:true}")
    private boolean enableIntelligenceEnrichment;

    // Metrics
    private Counter workflowStartedCounter;
    private Counter workflowCompletedCounter;
    private Counter workflowFailedCounter;
    private Timer workflowDurationTimer;

    // Workflow state management
    private final Map<String, WorkflowExecution> activeWorkflows = new ConcurrentHashMap<>();
    private final AtomicLong workflowIdGenerator = new AtomicLong(0);

    @PostConstruct
    public void initializeMetrics() {
        workflowStartedCounter = Counter.builder("threat_detection_workflows_started_total")
                .description("Total number of workflows started")
                .register(meterRegistry);

        workflowCompletedCounter = Counter.builder("threat_detection_workflows_completed_total")
                .description("Total number of workflows completed successfully")
                .register(meterRegistry);

        workflowFailedCounter = Counter.builder("threat_detection_workflows_failed_total")
                .description("Total number of workflows that failed")
                .register(meterRegistry);

        workflowDurationTimer = Timer.builder("threat_detection_workflow_duration")
                .description("Duration of workflow execution")
                .register(meterRegistry);

        log.info("Agent Coordinator initialized with max concurrent workflows: {}", maxConcurrentWorkflows);
    }

    /**
     * Enhanced network traffic analysis workflow with comprehensive threat detection pipeline.
     * Includes intelligence enrichment, pattern learning, and adaptive response.
     */
    @Async
    @Timed(name = "network_traffic_workflow", description = "Time taken for network traffic workflow")
    @CircuitBreaker(name = "network-analysis", fallbackMethod = "fallbackNetworkAnalysis")
    @Retry(name = "network-analysis")
    @RateLimiter(name = "network-analysis")
    public CompletableFuture<WorkflowResult<List<ResponseAction>>> processNetworkTrafficWorkflow(NetworkPacket packet) {
        String workflowId = generateWorkflowId("network-traffic");
        WorkflowExecution execution = startWorkflow(workflowId, "Network Traffic Analysis", packet);

        log.info("Starting enhanced network traffic analysis workflow {} for: {}", workflowId, packet.getSourceIp());
        workflowStartedCounter.increment();

        Timer.Sample sample = Timer.start(meterRegistry);

        return CompletableFuture.supplyAsync(() -> {
            try {
                // Step 1: Initial network analysis
                execution.addStep("network-analysis", "Analyzing network packet");
                ThreatAlert alert = orchestrator.<NetworkPacket, ThreatAlert>submitTask("network-monitor", packet)
                        .get(workflowTimeoutSeconds, java.util.concurrent.TimeUnit.SECONDS);

                if (alert == null) {
                    execution.complete("No threats detected");
                    workflowCompletedCounter.increment();
                    return WorkflowResult.<List<ResponseAction>>builder()
                            .workflowId(workflowId)
                            .success(true)
                            .result(Collections.emptyList())
                            .executionTime(Duration.between(execution.getStartTime(), LocalDateTime.now()))
                            .build();
                }

                log.info("Threat detected in workflow {}: {}", workflowId, alert.getDescription());
                execution.addStep("threat-detected", "Threat detected: " + alert.getDescription());

                // Step 2: Intelligence enrichment (if enabled)
                if (enableIntelligenceEnrichment) {
                    execution.addStep("intelligence-enrichment", "Enriching threat with intelligence data");
                    ThreatIntelligence intelligence = enrichThreatWithIntelligence(alert);
                    alert.setIntelligenceData(intelligence);
                }

                // Step 3: Pattern learning and model update
                execution.addStep("pattern-learning", "Updating detection patterns");
                updateDetectionPatterns(packet, alert);

                // Step 4: Generate response actions
                execution.addStep("response-generation", "Generating response actions");
                List<ResponseAction> actions = orchestrator.<ThreatAlert, List<ResponseAction>>submitTask(
                        "threat-response", alert)
                        .get(workflowTimeoutSeconds, java.util.concurrent.TimeUnit.SECONDS);

                // Step 5: Validate and prioritize actions
                execution.addStep("action-validation", "Validating and prioritizing actions");
                List<ResponseAction> validatedActions = validateAndPrioritizeActions(actions, alert);

                execution.complete("Workflow completed successfully with " + validatedActions.size() + " actions");
                workflowCompletedCounter.increment();

                return WorkflowResult.<List<ResponseAction>>builder()
                        .workflowId(workflowId)
                        .success(true)
                        .result(validatedActions)
                        .threatAlert(alert)
                        .executionTime(Duration.between(execution.getStartTime(), LocalDateTime.now()))
                        .build();

            } catch (Exception e) {
                log.error("Network traffic workflow {} failed", workflowId, e);
                execution.fail("Workflow failed: " + e.getMessage());
                workflowFailedCounter.increment();

                return WorkflowResult.<List<ResponseAction>>builder()
                        .workflowId(workflowId)
                        .success(false)
                        .error(e.getMessage())
                        .executionTime(Duration.between(execution.getStartTime(), LocalDateTime.now()))
                        .build();
            } finally {
                sample.stop(workflowDurationTimer);
                activeWorkflows.remove(workflowId);
            }
        });
    }

    /**
     * Enhanced user behavior analysis workflow with contextual analysis and risk scoring.
     */
    @Async
    @Timed(name = "user_behavior_workflow", description = "Time taken for user behavior workflow")
    @CircuitBreaker(name = "behavior-analysis", fallbackMethod = "fallbackBehaviorAnalysis")
    @Retry(name = "behavior-analysis")
    @RateLimiter(name = "behavior-analysis")
    public CompletableFuture<WorkflowResult<List<ResponseAction>>> processUserActivityWorkflow(UserActivity activity) {
        String workflowId = generateWorkflowId("user-behavior");
        WorkflowExecution execution = startWorkflow(workflowId, "User Behavior Analysis", activity);

        log.info("Starting enhanced user behavior analysis workflow {} for user: {}", workflowId, activity.getUserId());
        workflowStartedCounter.increment();

        Timer.Sample sample = Timer.start(meterRegistry);

        return CompletableFuture.supplyAsync(() -> {
            try {
                // Step 1: Behavioral analysis
                execution.addStep("behavior-analysis", "Analyzing user behavior patterns");
                ThreatAlert alert = orchestrator.<UserActivity, ThreatAlert>submitTask("behavior-analysis", activity)
                        .get(workflowTimeoutSeconds, java.util.concurrent.TimeUnit.SECONDS);

                if (alert == null) {
                    execution.complete("No behavioral anomalies detected");
                    workflowCompletedCounter.increment();
                    return WorkflowResult.<List<ResponseAction>>builder()
                            .workflowId(workflowId)
                            .success(true)
                            .result(Collections.emptyList())
                            .executionTime(Duration.between(execution.getStartTime(), LocalDateTime.now()))
                            .build();
                }

                log.info("Behavioral anomaly detected in workflow {}: {}", workflowId, alert.getDescription());
                execution.addStep("anomaly-detected", "Behavioral anomaly detected: " + alert.getDescription());

                // Step 2: Contextual risk assessment
                execution.addStep("risk-assessment", "Performing contextual risk assessment");
                RiskScore riskScore = calculateContextualRisk(activity, alert);
                alert.setRiskScore(riskScore);

                // Step 3: Historical pattern analysis
                execution.addStep("historical-analysis", "Analyzing historical patterns");
                UserBehaviorProfile profile = analyzeHistoricalBehavior(activity.getUserId());

                // Step 4: Generate adaptive response
                execution.addStep("adaptive-response", "Generating adaptive response actions");
                List<ResponseAction> actions = generateAdaptiveResponse(alert, profile, riskScore);

                execution.complete("Workflow completed with risk score: " + riskScore.getScore());
                workflowCompletedCounter.increment();

                return WorkflowResult.<List<ResponseAction>>builder()
                        .workflowId(workflowId)
                        .success(true)
                        .result(actions)
                        .threatAlert(alert)
                        .riskScore(riskScore)
                        .executionTime(Duration.between(execution.getStartTime(), LocalDateTime.now()))
                        .build();

            } catch (Exception e) {
                log.error("User behavior workflow {} failed", workflowId, e);
                execution.fail("Workflow failed: " + e.getMessage());
                workflowFailedCounter.increment();

                return WorkflowResult.<List<ResponseAction>>builder()
                        .workflowId(workflowId)
                        .success(false)
                        .error(e.getMessage())
                        .executionTime(Duration.between(execution.getStartTime(), LocalDateTime.now()))
                        .build();
            } finally {
                sample.stop(workflowDurationTimer);
                activeWorkflows.remove(workflowId);
            }
        });
    }

    /**
     * Comprehensive threat correlation workflow that analyzes multiple data sources.
     */
    @Async
    @Timed(name = "threat_correlation_workflow", description = "Time taken for threat correlation workflow")
    @CircuitBreaker(name = "threat-correlation", fallbackMethod = "fallbackThreatCorrelation")
    @Retry(name = "threat-correlation")
    public CompletableFuture<WorkflowResult<ThreatCorrelationReport>> correlateThreatDataWorkflow(
            List<NetworkPacket> networkData, List<UserActivity> userActivities) {

        String workflowId = generateWorkflowId("threat-correlation");
        WorkflowExecution execution = startWorkflow(workflowId, "Threat Correlation Analysis",
                Map.of("networkPackets", networkData.size(), "userActivities", userActivities.size()));

        log.info("Starting threat correlation workflow {} with {} network packets and {} user activities",
                workflowId, networkData.size(), userActivities.size());
        workflowStartedCounter.increment();

        Timer.Sample sample = Timer.start(meterRegistry);

        return CompletableFuture.supplyAsync(() -> {
            try {
                ThreatCorrelationReport report = new ThreatCorrelationReport();
                report.setWorkflowId(workflowId);
                report.setStartTime(LocalDateTime.now());

                // Step 1: Parallel analysis of network and user data
                execution.addStep("parallel-analysis", "Running parallel threat analysis");

                List<CompletableFuture<ThreatAlert>> networkAnalyses = networkData.stream()
                        .map(packet -> orchestrator.<NetworkPacket, ThreatAlert>submitTask("network-monitor", packet))
                        .toList();

                List<CompletableFuture<ThreatAlert>> behaviorAnalyses = userActivities.stream()
                        .map(activity -> orchestrator.<UserActivity, ThreatAlert>submitTask("behavior-analysis", activity))
                        .toList();

                // Wait for all analyses to complete
                List<ThreatAlert> networkAlerts = networkAnalyses.stream()
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)
                        .toList();

                List<ThreatAlert> behaviorAlerts = behaviorAnalyses.stream()
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)
                        .toList();

                // Step 2: Correlation analysis
                execution.addStep("correlation-analysis", "Correlating threat indicators");
                List<ThreatCorrelation> correlations = findThreatCorrelations(networkAlerts, behaviorAlerts);

                // Step 3: Risk aggregation
                execution.addStep("risk-aggregation", "Aggregating risk scores");
                double aggregatedRisk = calculateAggregatedRisk(correlations);

                // Step 4: Generate comprehensive response
                execution.addStep("comprehensive-response", "Generating comprehensive response plan");
                List<ResponseAction> responseActions = generateComprehensiveResponse(correlations, aggregatedRisk);

                report.setNetworkAlerts(networkAlerts);
                report.setBehaviorAlerts(behaviorAlerts);
                report.setCorrelations(correlations);
                report.setAggregatedRiskScore(aggregatedRisk);
                report.setResponseActions(responseActions);
                report.setEndTime(LocalDateTime.now());

                execution.complete("Correlation analysis completed with " + correlations.size() + " correlations found");
                workflowCompletedCounter.increment();

                return WorkflowResult.<ThreatCorrelationReport>builder()
                        .workflowId(workflowId)
                        .success(true)
                        .result(report)
                        .executionTime(Duration.between(execution.getStartTime(), LocalDateTime.now()))
                        .build();

            } catch (Exception e) {
                log.error("Threat correlation workflow {} failed", workflowId, e);
                execution.fail("Workflow failed: " + e.getMessage());
                workflowFailedCounter.increment();

                return WorkflowResult.<ThreatCorrelationReport>builder()
                        .workflowId(workflowId)
                        .success(false)
                        .error(e.getMessage())
                        .executionTime(Duration.between(execution.getStartTime(), LocalDateTime.now()))
                        .build();
            } finally {
                sample.stop(workflowDurationTimer);
                activeWorkflows.remove(workflowId);
            }
        });
    }

    /**
     * Scheduled job to start all agents at application startup.
     */
    @Scheduled(initialDelay = 10000, fixedDelay = Long.MAX_VALUE)
    public void startupAgents() {
        log.info("Starting all AI agents");
        orchestrator.startAllAgents();
    }

    /**
     * Scheduled cleanup of completed workflows.
     */
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void cleanupCompletedWorkflows() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(1);
        int removed = 0;

        Iterator<Map.Entry<String, WorkflowExecution>> iterator = activeWorkflows.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, WorkflowExecution> entry = iterator.next();
            WorkflowExecution execution = entry.getValue();

            if (execution.isCompleted() && execution.getEndTime() != null && execution.getEndTime().isBefore(cutoff)) {
                iterator.remove();
                removed++;
            }
        }

        if (removed > 0) {
            log.debug("Cleaned up {} completed workflows", removed);
        }
    }
    // ==================== WORKFLOW MANAGEMENT METHODS ====================

    private String generateWorkflowId(String workflowType) {
        return workflowType + "-" + workflowIdGenerator.incrementAndGet() + "-" + System.currentTimeMillis();
    }

    private WorkflowExecution startWorkflow(String workflowId, String workflowName, Object input) {
        if (activeWorkflows.size() >= maxConcurrentWorkflows) {
            throw new IllegalStateException("Maximum concurrent workflows exceeded: " + maxConcurrentWorkflows);
        }

        WorkflowExecution execution = WorkflowExecution.builder()
                .workflowId(workflowId)
                .workflowName(workflowName)
                .startTime(LocalDateTime.now())
                .status(WorkflowStatus.RUNNING)
                .input(input)
                .steps(new ArrayList<>())
                .build();

        activeWorkflows.put(workflowId, execution);
        log.debug("Started workflow: {} ({})", workflowId, workflowName);
        return execution;
    }

    @Cacheable(value = "workflow-status", key = "#workflowId")
    public Optional<WorkflowExecution> getWorkflowStatus(String workflowId) {
        return Optional.ofNullable(activeWorkflows.get(workflowId));
    }

    public Map<String, WorkflowExecution> getAllActiveWorkflows() {
        return new HashMap<>(activeWorkflows);
    }

    public WorkflowStatistics getWorkflowStatistics() {
        return WorkflowStatistics.builder()
                .activeWorkflows(activeWorkflows.size())
                .totalStarted((long) workflowStartedCounter.count())
                .totalCompleted((long) workflowCompletedCounter.count())
                .totalFailed((long) workflowFailedCounter.count())
                .averageDuration(workflowDurationTimer.mean(java.util.concurrent.TimeUnit.MILLISECONDS))
                .build();
    }

    // ==================== INTELLIGENCE AND ANALYSIS METHODS ====================

    private ThreatIntelligence enrichThreatWithIntelligence(ThreatAlert alert) {
        try {
            IntelligenceQuery query = IntelligenceQuery.builder()
                    .indicator(alert.getSourceIp())
                    .indicatorType(IntelligenceQuery.IndicatorType.IP)
                    .build();

            return orchestrator.<IntelligenceQuery, ThreatIntelligence>submitTask("threat-intelligence", query)
                    .get(30, java.util.concurrent.TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Failed to enrich threat with intelligence: {}", e.getMessage());
            return ThreatIntelligence.builder()
                    .indicator(alert.getSourceIp())
                    .reputation("UNKNOWN")
                    .confidence(0.0)
                    .build();
        }
    }

    private void updateDetectionPatterns(NetworkPacket packet, ThreatAlert alert) {
        try {
            PatternLearningData learningData = PatternLearningData.builder()
                    .networkPacket(packet)
                    .threatAlert(alert)
                    .timestamp(LocalDateTime.now())
                    .build();

            orchestrator.submitTask("pattern-learning", learningData);
        } catch (Exception e) {
            log.warn("Failed to update detection patterns: {}", e.getMessage());
        }
    }

    private List<ResponseAction> validateAndPrioritizeActions(List<ResponseAction> actions, ThreatAlert alert) {
        return actions.stream()
                .filter(this::isActionValid)
                .sorted((a1, a2) -> Integer.compare(a2.getPriority(), a1.getPriority()))
                .limit(10) // Limit to top 10 actions
                .toList();
    }

    private boolean isActionValid(ResponseAction action) {
        // Implement validation logic
        return action != null &&
               action.getActionType() != null &&
               action.getDescription() != null &&
               !action.getDescription().trim().isEmpty();
    }

    private RiskScore calculateContextualRisk(UserActivity activity, ThreatAlert alert) {
        // Implement sophisticated risk calculation
        double baseRisk = alert.getSeverity().ordinal() * 0.25;
        double timeRisk = calculateTimeBasedRisk(activity.getTimestamp());
        double locationRisk = calculateLocationBasedRisk(activity.getIpAddress());
        double behaviorRisk = calculateBehaviorBasedRisk(activity);

        double totalRisk = Math.min(1.0, baseRisk + timeRisk + locationRisk + behaviorRisk);

        return RiskScore.builder()
                .score(totalRisk)
                .baseRisk(baseRisk)
                .timeRisk(timeRisk)
                .locationRisk(locationRisk)
                .behaviorRisk(behaviorRisk)
                .calculatedAt(LocalDateTime.now())
                .build();
    }

    private double calculateTimeBasedRisk(LocalDateTime timestamp) {
        // Higher risk for activities outside business hours
        int hour = timestamp.getHour();
        if (hour < 6 || hour > 22) {
            return 0.2;
        } else if (hour < 8 || hour > 18) {
            return 0.1;
        }
        return 0.0;
    }

    private double calculateLocationBasedRisk(String ipAddress) {
        // Implement geolocation-based risk assessment
        // This is a simplified implementation
        if (ipAddress.startsWith("10.") || ipAddress.startsWith("192.168.") || ipAddress.startsWith("172.")) {
            return 0.0; // Internal network
        }
        return 0.15; // External network
    }

    private double calculateBehaviorBasedRisk(UserActivity activity) {
        // Implement behavior-based risk calculation
        switch (activity.getActivityType()) {
            case "ADMIN_ACCESS":
                return 0.3;
            case "DATA_EXPORT":
                return 0.25;
            case "PRIVILEGE_ESCALATION":
                return 0.4;
            default:
                return 0.1;
        }
    }

    private UserBehaviorProfile analyzeHistoricalBehavior(String userId) {
        // Implement historical behavior analysis
        return UserBehaviorProfile.builder()
                .userId(userId)
                .averageSessionDuration(Duration.ofMinutes(45))
                .typicalAccessHours(Set.of(9, 10, 11, 14, 15, 16, 17))
                .frequentResources(Set.of("/dashboard", "/reports", "/profile"))
                .riskProfile("LOW")
                .lastAnalyzed(LocalDateTime.now())
                .build();
    }

    private List<ResponseAction> generateAdaptiveResponse(ThreatAlert alert, UserBehaviorProfile profile, RiskScore riskScore) {
        List<ResponseAction> actions = new ArrayList<>();

        // Base response actions
        if (riskScore.getScore() > 0.8) {
            actions.add(ResponseAction.builder()
                    .actionType("IMMEDIATE_LOCKOUT")
                    .description("Immediately lock user account due to high risk")
                    .priority(10)
                    .automated(true)
                    .build());
        } else if (riskScore.getScore() > 0.6) {
            actions.add(ResponseAction.builder()
                    .actionType("REQUIRE_MFA")
                    .description("Require additional authentication")
                    .priority(8)
                    .automated(true)
                    .build());
        } else if (riskScore.getScore() > 0.4) {
            actions.add(ResponseAction.builder()
                    .actionType("MONITOR_CLOSELY")
                    .description("Increase monitoring for this user")
                    .priority(5)
                    .automated(true)
                    .build());
        }

        // Add notification action
        actions.add(ResponseAction.builder()
                .actionType("NOTIFY_SECURITY_TEAM")
                .description("Notify security team of behavioral anomaly")
                .priority(7)
                .automated(true)
                .build());

        return actions;
    }

    private List<ThreatCorrelation> findThreatCorrelations(List<ThreatAlert> networkAlerts, List<ThreatAlert> behaviorAlerts) {
        List<ThreatCorrelation> correlations = new ArrayList<>();

        // Time-based correlation
        for (ThreatAlert networkAlert : networkAlerts) {
            for (ThreatAlert behaviorAlert : behaviorAlerts) {
                if (isTimeCorrelated(networkAlert, behaviorAlert)) {
                    correlations.add(ThreatCorrelation.builder()
                            .correlationType("TIME_BASED")
                            .networkAlert(networkAlert)
                            .behaviorAlert(behaviorAlert)
                            .confidence(0.7)
                            .description("Alerts occurred within suspicious time window")
                            .build());
                }
            }
        }

        // IP-based correlation
        for (ThreatAlert networkAlert : networkAlerts) {
            for (ThreatAlert behaviorAlert : behaviorAlerts) {
                if (isIpCorrelated(networkAlert, behaviorAlert)) {
                    correlations.add(ThreatCorrelation.builder()
                            .correlationType("IP_BASED")
                            .networkAlert(networkAlert)
                            .behaviorAlert(behaviorAlert)
                            .confidence(0.9)
                            .description("Alerts from same IP address")
                            .build());
                }
            }
        }

        return correlations;
    }

    private boolean isTimeCorrelated(ThreatAlert alert1, ThreatAlert alert2) {
        Duration timeDiff = Duration.between(alert1.getDetectionTime(), alert2.getDetectionTime());
        return timeDiff.abs().toMinutes() <= 30; // Within 30 minutes
    }

    private boolean isIpCorrelated(ThreatAlert networkAlert, ThreatAlert behaviorAlert) {
        return networkAlert.getSourceIp() != null &&
               networkAlert.getSourceIp().equals(behaviorAlert.getSourceIp());
    }

    private double calculateAggregatedRisk(List<ThreatCorrelation> correlations) {
        if (correlations.isEmpty()) {
            return 0.0;
        }

        double totalRisk = correlations.stream()
                .mapToDouble(correlation -> correlation.getConfidence() * 0.5)
                .sum();

        return Math.min(1.0, totalRisk);
    }

    private List<ResponseAction> generateComprehensiveResponse(List<ThreatCorrelation> correlations, double aggregatedRisk) {
        List<ResponseAction> actions = new ArrayList<>();

        if (aggregatedRisk > 0.8) {
            actions.add(ResponseAction.builder()
                    .actionType("INCIDENT_RESPONSE")
                    .description("Initiate full incident response procedure")
                    .priority(10)
                    .automated(false)
                    .build());
        }

        if (aggregatedRisk > 0.6) {
            actions.add(ResponseAction.builder()
                    .actionType("NETWORK_ISOLATION")
                    .description("Isolate affected network segments")
                    .priority(9)
                    .automated(true)
                    .build());
        }

        actions.add(ResponseAction.builder()
                .actionType("THREAT_HUNTING")
                .description("Initiate threat hunting based on correlations")
                .priority(7)
                .automated(false)
                .build());

        return actions;
    }

    // ==================== FALLBACK METHODS ====================

    public CompletableFuture<WorkflowResult<List<ResponseAction>>> fallbackNetworkAnalysis(NetworkPacket packet, Exception ex) {
        log.warn("Network analysis workflow fallback triggered for packet: {}", packet.getSourceIp(), ex);

        return CompletableFuture.completedFuture(
                WorkflowResult.<List<ResponseAction>>builder()
                        .workflowId("fallback-network-" + System.currentTimeMillis())
                        .success(false)
                        .error("Service temporarily unavailable: " + ex.getMessage())
                        .result(Collections.emptyList())
                        .executionTime(Duration.ZERO)
                        .build()
        );
    }

    public CompletableFuture<WorkflowResult<List<ResponseAction>>> fallbackBehaviorAnalysis(UserActivity activity, Exception ex) {
        log.warn("Behavior analysis workflow fallback triggered for user: {}", activity.getUserId(), ex);

        return CompletableFuture.completedFuture(
                WorkflowResult.<List<ResponseAction>>builder()
                        .workflowId("fallback-behavior-" + System.currentTimeMillis())
                        .success(false)
                        .error("Service temporarily unavailable: " + ex.getMessage())
                        .result(Collections.emptyList())
                        .executionTime(Duration.ZERO)
                        .build()
        );
    }

    public CompletableFuture<WorkflowResult<ThreatCorrelationReport>> fallbackThreatCorrelation(
            List<NetworkPacket> networkData, List<UserActivity> userActivities, Exception ex) {
        log.warn("Threat correlation workflow fallback triggered", ex);

        return CompletableFuture.completedFuture(
                WorkflowResult.<ThreatCorrelationReport>builder()
                        .workflowId("fallback-correlation-" + System.currentTimeMillis())
                        .success(false)
                        .error("Service temporarily unavailable: " + ex.getMessage())
                        .executionTime(Duration.ZERO)
                        .build()
        );
    }
}
