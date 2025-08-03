package com.mriridescent.threatdetection.controller;

import com.mriridescent.threatdetection.agent.coordinator.AgentCoordinator;
import com.mriridescent.threatdetection.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * REST API controller for the enhanced Agent Coordinator.
 * Provides endpoints for workflow management and threat analysis coordination.
 */
@RestController
@RequestMapping("/api/v1/coordinator")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Agent Coordinator", description = "Advanced workflow coordination and threat analysis")
public class AgentCoordinatorController {

    private final AgentCoordinator agentCoordinator;

    /**
     * Submit network traffic for comprehensive analysis workflow.
     */
    @PostMapping("/network-analysis")
    @Operation(summary = "Analyze network traffic", 
               description = "Submit network packet for comprehensive threat analysis workflow")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Analysis completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid network packet data"),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded"),
        @ApiResponse(responseCode = "503", description = "Service temporarily unavailable")
    })
    @PreAuthorize("hasRole('ANALYST') or hasRole('ADMIN')")
    public CompletableFuture<ResponseEntity<WorkflowResult<List<ResponseAction>>>> analyzeNetworkTraffic(
            @Valid @RequestBody NetworkPacket packet) {
        
        log.info("Received network analysis request for packet: {}", packet.getPacketId());
        
        return agentCoordinator.processNetworkTrafficWorkflow(packet)
                .thenApply(result -> {
                    if (result.isSuccessful()) {
                        return ResponseEntity.ok(result);
                    } else {
                        return ResponseEntity.status(503).body(result);
                    }
                });
    }

    /**
     * Submit user activity for behavioral analysis workflow.
     */
    @PostMapping("/behavior-analysis")
    @Operation(summary = "Analyze user behavior", 
               description = "Submit user activity for behavioral anomaly detection workflow")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Analysis completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid user activity data"),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded"),
        @ApiResponse(responseCode = "503", description = "Service temporarily unavailable")
    })
    @PreAuthorize("hasRole('ANALYST') or hasRole('ADMIN')")
    public CompletableFuture<ResponseEntity<WorkflowResult<List<ResponseAction>>>> analyzeUserBehavior(
            @Valid @RequestBody UserActivity activity) {
        
        log.info("Received behavior analysis request for user: {}", activity.getUserId());
        
        return agentCoordinator.processUserActivityWorkflow(activity)
                .thenApply(result -> {
                    if (result.isSuccessful()) {
                        return ResponseEntity.ok(result);
                    } else {
                        return ResponseEntity.status(503).body(result);
                    }
                });
    }

    /**
     * Submit data for comprehensive threat correlation analysis.
     */
    @PostMapping("/threat-correlation")
    @Operation(summary = "Correlate threat data", 
               description = "Analyze multiple data sources for threat correlations")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Correlation analysis completed"),
        @ApiResponse(responseCode = "400", description = "Invalid correlation request"),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded"),
        @ApiResponse(responseCode = "503", description = "Service temporarily unavailable")
    })
    @PreAuthorize("hasRole('SENIOR_ANALYST') or hasRole('ADMIN')")
    public CompletableFuture<ResponseEntity<WorkflowResult<ThreatCorrelationReport>>> correlateThreatData(
            @Valid @RequestBody ThreatCorrelationRequest request) {
        
        log.info("Received threat correlation request with {} network packets and {} user activities", 
                request.getNetworkPackets().size(), request.getUserActivities().size());
        
        return agentCoordinator.correlateThreatDataWorkflow(
                request.getNetworkPackets(), request.getUserActivities())
                .thenApply(result -> {
                    if (result.isSuccessful()) {
                        return ResponseEntity.ok(result);
                    } else {
                        return ResponseEntity.status(503).body(result);
                    }
                });
    }

    /**
     * Get the status of a specific workflow.
     */
    @GetMapping("/workflows/{workflowId}")
    @Operation(summary = "Get workflow status", 
               description = "Retrieve the current status and progress of a workflow")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Workflow status retrieved"),
        @ApiResponse(responseCode = "404", description = "Workflow not found")
    })
    @PreAuthorize("hasRole('ANALYST') or hasRole('ADMIN')")
    public ResponseEntity<WorkflowExecution> getWorkflowStatus(
            @Parameter(description = "Workflow ID") @PathVariable @NotBlank String workflowId) {
        
        Optional<WorkflowExecution> workflow = agentCoordinator.getWorkflowStatus(workflowId);
        
        return workflow.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all active workflows.
     */
    @GetMapping("/workflows")
    @Operation(summary = "List active workflows", 
               description = "Retrieve all currently active workflows")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active workflows retrieved")
    })
    @PreAuthorize("hasRole('ANALYST') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, WorkflowExecution>> getActiveWorkflows() {
        Map<String, WorkflowExecution> activeWorkflows = agentCoordinator.getAllActiveWorkflows();
        return ResponseEntity.ok(activeWorkflows);
    }

    /**
     * Get workflow execution statistics.
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get workflow statistics", 
               description = "Retrieve comprehensive workflow execution statistics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    })
    @PreAuthorize("hasRole('ANALYST') or hasRole('ADMIN')")
    public ResponseEntity<WorkflowStatistics> getWorkflowStatistics() {
        WorkflowStatistics statistics = agentCoordinator.getWorkflowStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Health check endpoint for the coordinator.
     */
    @GetMapping("/health")
    @Operation(summary = "Coordinator health check", 
               description = "Check the health status of the agent coordinator")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Coordinator is healthy"),
        @ApiResponse(responseCode = "503", description = "Coordinator is unhealthy")
    })
    public ResponseEntity<Map<String, Object>> healthCheck() {
        WorkflowStatistics stats = agentCoordinator.getWorkflowStatistics();
        Map<String, WorkflowExecution> activeWorkflows = agentCoordinator.getAllActiveWorkflows();
        
        Map<String, Object> health = Map.of(
            "status", "UP",
            "activeWorkflows", activeWorkflows.size(),
            "totalProcessed", stats.getTotalProcessed(),
            "successRate", stats.getSuccessRate(),
            "averageDuration", stats.getAverageDuration()
        );
        
        return ResponseEntity.ok(health);
    }
}

/**
 * Request model for threat correlation analysis.
 */
@lombok.Data
@lombok.Builder
class ThreatCorrelationRequest {
    @Valid
    private List<NetworkPacket> networkPackets;
    
    @Valid
    private List<UserActivity> userActivities;
    
    private String timeWindow = "PT30M"; // Default 30 minutes
    private List<String> correlationTypes = List.of("TIME_BASED", "IP_BASED");
}
