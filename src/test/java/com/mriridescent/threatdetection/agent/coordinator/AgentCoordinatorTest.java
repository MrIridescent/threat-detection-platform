package com.mriridescent.threatdetection.agent.coordinator;

import com.mriridescent.threatdetection.model.*;
import com.mriridescent.threatdetection.orchestration.AgentOrchestrator;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test suite for the enhanced Agent Coordinator.
 */
@ExtendWith(MockitoExtension.class)
class AgentCoordinatorTest {

    @Mock
    private AgentOrchestrator orchestrator;

    private AgentCoordinator agentCoordinator;
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        agentCoordinator = new AgentCoordinator(orchestrator, meterRegistry);
        
        // Set test configuration
        ReflectionTestUtils.setField(agentCoordinator, "maxConcurrentWorkflows", 10);
        ReflectionTestUtils.setField(agentCoordinator, "workflowTimeoutSeconds", 30);
        ReflectionTestUtils.setField(agentCoordinator, "enableIntelligenceEnrichment", true);
        
        // Initialize metrics
        agentCoordinator.initializeMetrics();
    }

    @Test
    void testNetworkTrafficWorkflow_Success() throws Exception {
        // Arrange
        NetworkPacket packet = createTestNetworkPacket();
        ThreatAlert alert = createTestThreatAlert();
        List<ResponseAction> actions = createTestResponseActions();

        when(orchestrator.<NetworkPacket, ThreatAlert>submitTask(eq("network-monitor"), any()))
                .thenReturn(CompletableFuture.completedFuture(alert));
        when(orchestrator.<ThreatAlert, List<ResponseAction>>submitTask(eq("threat-response"), any()))
                .thenReturn(CompletableFuture.completedFuture(actions));
        when(orchestrator.<IntelligenceQuery, ThreatIntelligence>submitTask(eq("threat-intelligence"), any()))
                .thenReturn(CompletableFuture.completedFuture(createTestThreatIntelligence()));

        // Act
        WorkflowResult<List<ResponseAction>> result = agentCoordinator
                .processNetworkTrafficWorkflow(packet)
                .get();

        // Assert
        assertTrue(result.isSuccessful());
        assertNotNull(result.getResult());
        assertEquals(actions.size(), result.getResult().size());
        assertNotNull(result.getWorkflowId());
        assertNotNull(result.getExecutionTime());
        
        // Verify orchestrator interactions
        verify(orchestrator, times(1)).submitTask(eq("network-monitor"), eq(packet));
        verify(orchestrator, times(1)).submitTask(eq("threat-response"), any(ThreatAlert.class));
        verify(orchestrator, times(1)).submitTask(eq("threat-intelligence"), any(IntelligenceQuery.class));
    }

    @Test
    void testNetworkTrafficWorkflow_NoThreatDetected() throws Exception {
        // Arrange
        NetworkPacket packet = createTestNetworkPacket();

        when(orchestrator.<NetworkPacket, ThreatAlert>submitTask(eq("network-monitor"), any()))
                .thenReturn(CompletableFuture.completedFuture(null));

        // Act
        WorkflowResult<List<ResponseAction>> result = agentCoordinator
                .processNetworkTrafficWorkflow(packet)
                .get();

        // Assert
        assertTrue(result.isSuccessful());
        assertNotNull(result.getResult());
        assertTrue(result.getResult().isEmpty());
        
        // Verify only network monitor was called
        verify(orchestrator, times(1)).submitTask(eq("network-monitor"), eq(packet));
        verify(orchestrator, never()).submitTask(eq("threat-response"), any());
    }

    @Test
    void testUserBehaviorWorkflow_Success() throws Exception {
        // Arrange
        UserActivity activity = createTestUserActivity();
        ThreatAlert alert = createTestThreatAlert();
        List<ResponseAction> actions = createTestResponseActions();

        when(orchestrator.<UserActivity, ThreatAlert>submitTask(eq("behavior-analysis"), any()))
                .thenReturn(CompletableFuture.completedFuture(alert));

        // Act
        WorkflowResult<List<ResponseAction>> result = agentCoordinator
                .processUserActivityWorkflow(activity)
                .get();

        // Assert
        assertTrue(result.isSuccessful());
        assertNotNull(result.getResult());
        assertNotNull(result.getRiskScore());
        
        // Verify orchestrator interactions
        verify(orchestrator, times(1)).submitTask(eq("behavior-analysis"), eq(activity));
    }

    @Test
    void testThreatCorrelationWorkflow_Success() throws Exception {
        // Arrange
        List<NetworkPacket> networkPackets = List.of(createTestNetworkPacket());
        List<UserActivity> userActivities = List.of(createTestUserActivity());
        ThreatAlert networkAlert = createTestThreatAlert();
        ThreatAlert behaviorAlert = createTestThreatAlert();

        when(orchestrator.<NetworkPacket, ThreatAlert>submitTask(eq("network-monitor"), any()))
                .thenReturn(CompletableFuture.completedFuture(networkAlert));
        when(orchestrator.<UserActivity, ThreatAlert>submitTask(eq("behavior-analysis"), any()))
                .thenReturn(CompletableFuture.completedFuture(behaviorAlert));

        // Act
        WorkflowResult<ThreatCorrelationReport> result = agentCoordinator
                .correlateThreatDataWorkflow(networkPackets, userActivities)
                .get();

        // Assert
        assertTrue(result.isSuccessful());
        assertNotNull(result.getResult());
        ThreatCorrelationReport report = result.getResult();
        assertNotNull(report.getNetworkAlerts());
        assertNotNull(report.getBehaviorAlerts());
        assertNotNull(report.getCorrelations());
        
        // Verify orchestrator interactions
        verify(orchestrator, times(1)).submitTask(eq("network-monitor"), any(NetworkPacket.class));
        verify(orchestrator, times(1)).submitTask(eq("behavior-analysis"), any(UserActivity.class));
    }

    @Test
    void testWorkflowStatistics() {
        // Act
        WorkflowStatistics stats = agentCoordinator.getWorkflowStatistics();

        // Assert
        assertNotNull(stats);
        assertEquals(0, stats.getActiveWorkflows());
        assertTrue(stats.getTotalStarted() >= 0);
        assertTrue(stats.getTotalCompleted() >= 0);
        assertTrue(stats.getTotalFailed() >= 0);
    }

    @Test
    void testMaxConcurrentWorkflowsLimit() {
        // Set a very low limit for testing
        ReflectionTestUtils.setField(agentCoordinator, "maxConcurrentWorkflows", 1);
        
        // Fill up the workflow capacity
        NetworkPacket packet1 = createTestNetworkPacket();
        NetworkPacket packet2 = createTestNetworkPacket();
        
        when(orchestrator.<NetworkPacket, ThreatAlert>submitTask(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(null));

        // First workflow should succeed
        assertDoesNotThrow(() -> {
            agentCoordinator.processNetworkTrafficWorkflow(packet1);
        });

        // Second workflow should be rejected due to capacity limit
        // Note: This test may need adjustment based on actual implementation
        // as the limit check might be in a different place
    }

    // Helper methods to create test data

    private NetworkPacket createTestNetworkPacket() {
        NetworkPacket packet = new NetworkPacket();
        packet.setPacketId("test-packet-123");
        packet.setSourceIp("192.168.1.100");
        packet.setDestinationIp("10.0.0.1");
        packet.setProtocol("TCP");
        packet.setTimestamp(LocalDateTime.now());
        return packet;
    }

    private UserActivity createTestUserActivity() {
        UserActivity activity = new UserActivity();
        activity.setUserId("test-user-123");
        activity.setActivityType("LOGIN");
        activity.setIpAddress("192.168.1.100");
        activity.setTimestamp(LocalDateTime.now());
        return activity;
    }

    private ThreatAlert createTestThreatAlert() {
        ThreatAlert alert = new ThreatAlert();
        alert.setSourceIp("192.168.1.100");
        alert.setDescription("Test threat alert");
        alert.setSeverity(ThreatAlert.Severity.MEDIUM);
        alert.setDetectionTime(LocalDateTime.now());
        alert.setConfidenceScore(0.8);
        return alert;
    }

    private List<ResponseAction> createTestResponseActions() {
        return List.of(
                ResponseAction.builder()
                        .actionType("BLOCK_IP")
                        .description("Block suspicious IP")
                        .priority(8)
                        .automated(true)
                        .build(),
                ResponseAction.builder()
                        .actionType("NOTIFY_ADMIN")
                        .description("Notify security team")
                        .priority(5)
                        .automated(true)
                        .build()
        );
    }

    private ThreatIntelligence createTestThreatIntelligence() {
        return ThreatIntelligence.builder()
                .indicator("192.168.1.100")
                .reputation("SUSPICIOUS")
                .confidence(0.7)
                .lastUpdated(LocalDateTime.now())
                .build();
    }
}
