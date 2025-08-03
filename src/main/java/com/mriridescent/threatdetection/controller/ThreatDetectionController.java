package com.mriridescent.threatdetection.controller;

import com.mriridescent.threatdetection.model.IntelligenceQuery;
import com.mriridescent.threatdetection.model.NetworkPacket;
import com.mriridescent.threatdetection.model.ThreatAlert;
import com.mriridescent.threatdetection.model.UserActivity;
import com.mriridescent.threatdetection.orchestration.AgentOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

/**
 * REST API controller for the threat detection system.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class ThreatDetectionController {

    private final AgentOrchestrator orchestrator;

    /**
     * Submit network traffic for analysis.
     */
    @PostMapping("/analyze/network")
    public CompletableFuture<ResponseEntity<ThreatAlert>> analyzeNetworkTraffic(
            @RequestBody NetworkPacket packet) {

        log.info("Received network packet for analysis from: {}", packet.getSourceIp());

        return orchestrator.<NetworkPacket, ThreatAlert>submitTask("network-monitor", packet)
                .thenApply(alert -> {
                    if (alert == null) {
                        return ResponseEntity.ok().build();
                    }
                    return ResponseEntity.ok(alert);
                });
    }

    /**
     * Submit user activity for behavioral analysis.
     */
    @PostMapping("/analyze/behavior")
    public CompletableFuture<ResponseEntity<ThreatAlert>> analyzeUserBehavior(
            @RequestBody UserActivity activity) {

        log.info("Received user activity for analysis: {} by {}", 
                activity.getActivityType(), activity.getUserId());

        return orchestrator.<UserActivity, ThreatAlert>submitTask("behavior-analysis", activity)
                .thenApply(alert -> {
                    if (alert == null) {
                        return ResponseEntity.ok().build();
                    }
                    return ResponseEntity.ok(alert);
                });
    }

    /**
     * Query threat intelligence for an indicator.
     */
    @PostMapping("/intelligence/query")
    public CompletableFuture<ResponseEntity<?>> queryThreatIntelligence(
            @RequestBody IntelligenceQuery query) {

        log.info("Received threat intelligence query for: {}", query.getIndicator());

        return orchestrator.submitTask("threat-intelligence", query)
                .thenApply(ResponseEntity::ok);
    }

    /**
     * Get status of all agents.
     */
    @GetMapping("/agents/status")
    public ResponseEntity<?> getAgentStatus() {
        return ResponseEntity.ok(orchestrator.getAllAgentStatus());
    }
}
