package com.mriridescent.threatdetection.demo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Demo dashboard data for showcasing platform capabilities
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemoDashboardData {
    
    // High-level metrics
    private int totalCampaigns;
    private int activeCampaigns;
    private int criticalThreats;
    private int totalVictims;
    
    // Distribution data
    private Map<String, Integer> campaignsByThreatLevel;
    private Map<String, Integer> campaignsByStatus;
    private Map<String, Integer> geographicDistribution;
    
    // Recent activity
    private List<CampaignSummary> recentCampaigns;
    private List<ThreatTrendData> threatTrends;
    
    // AI/ML performance
    private List<ModelPerformanceMetric> aiModelPerformance;
    
    // Real-time stats
    private RealtimeStats realtimeStats;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CampaignSummary {
        private Long id;
        private String name;
        private String threatLevel;
        private String status;
        private LocalDateTime discoveredAt;
        private int estimatedVictims;
        private String targetSector;
        private double aiConfidence;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThreatTrendData {
        private LocalDateTime date;
        private int threatCount;
        private int criticalThreats;
        private double averageThreatScore;
        private int newCampaigns;
        private int mitigatedThreats;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelPerformanceMetric {
        private String modelName;
        private String modelType;
        private double accuracy;
        private double precision;
        private double recall;
        private double f1Score;
        private LocalDateTime lastTrained;
        private int predictionsToday;
        private double averageInferenceTime;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RealtimeStats {
        private int threatsDetectedToday;
        private int emailsAnalyzedToday;
        private double averageResponseTime;
        private int activeAnalysts;
        private int systemLoad;
        private String systemHealth;
    }
}

/**
 * Network visualization data for campaign infrastructure
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class NetworkVisualizationData {
    private Long campaignId;
    private String campaignName;
    private List<NetworkNode> nodes;
    private List<NetworkEdge> edges;
    private NetworkMetrics networkMetrics;
    private List<String> aiInsights;
    private List<CriticalPath> criticalPaths;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NetworkNode {
        private String id;
        private String label;
        private String type;
        private String status;
        private double riskScore;
        private int size;
        private String color;
        private Map<String, Object> metadata;
        private List<String> tags;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NetworkEdge {
        private String from;
        private String to;
        private double weight;
        private String type;
        private String color;
        private boolean dashed;
        private Map<String, Object> metadata;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NetworkMetrics {
        private int totalNodes;
        private int totalConnections;
        private double averageConnections;
        private double networkDensity;
        private double clusteringCoefficient;
        private int connectedComponents;
        private List<String> centralNodes;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CriticalPath {
        private List<String> nodeIds;
        private double importance;
        private String description;
        private String recommendedAction;
    }
}

/**
 * AI performance data for demo showcase
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class AIPerformanceData {
    private List<ModelMetrics> modelMetrics;
    private List<AccuracyTrend> predictionAccuracy;
    private List<DetectionRate> threatDetectionRates;
    private List<ModelComparison> modelComparison;
    private RealtimeAIMetrics realtimeMetrics;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelMetrics {
        private String modelName;
        private String version;
        private double accuracy;
        private double precision;
        private double recall;
        private double f1Score;
        private int totalPredictions;
        private double averageInferenceTime;
        private LocalDateTime lastUpdated;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccuracyTrend {
        private LocalDateTime timestamp;
        private double accuracy;
        private String modelName;
        private int sampleSize;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetectionRate {
        private LocalDateTime timestamp;
        private int threatsDetected;
        private int falsePositives;
        private int falseNegatives;
        private double detectionRate;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelComparison {
        private String modelName;
        private String baseline;
        private double improvement;
        private String metric;
        private String significance;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RealtimeAIMetrics {
        private int activeModels;
        private int predictionsPerSecond;
        private double averageConfidence;
        private int queuedRequests;
        private String systemStatus;
    }
}

/**
 * Threat simulation data for interactive demos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ThreatSimulationResult {
    private String scenario;
    private int detectionTime; // milliseconds
    private double threatScore;
    private AIAnalysisResult aiAnalysis;
    private List<String> recommendedActions;
    private String confidenceLevel;
    private LocalDateTime timestamp;
    private List<DetectionStep> detectionSteps;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AIAnalysisResult {
        private String primaryThreat;
        private List<String> indicators;
        private Map<String, Double> featureScores;
        private String attribution;
        private List<String> similarThreats;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetectionStep {
        private String step;
        private String description;
        private int timeElapsed;
        private String result;
        private double confidence;
    }
}

/**
 * Request for threat simulation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ThreatSimulationRequest {
    private String scenario;
    private Map<String, String> parameters;
    private String complexity;
    private boolean includeAttribution;
}

/**
 * Campaign prediction data combining multiple AI analyses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class CampaignPredictionData {
    private Long campaignId;
    private Object evolutionPrediction; // CampaignEvolutionPrediction
    private List<Object> similarCampaigns; // List<SimilarCampaignMatch>
    private Object infrastructureAnalysis; // InfrastructurePatternAnalysis
    private RiskAssessment riskAssessment;
    private TimelineProjection timelineProjection;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RiskAssessment {
        private double overallRisk;
        private List<RiskFactor> riskFactors;
        private String riskLevel;
        private List<String> mitigationStrategies;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RiskFactor {
        private String factor;
        private double impact;
        private double likelihood;
        private String description;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimelineProjection {
        private List<ProjectedEvent> events;
        private int projectionDays;
        private double confidence;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectedEvent {
        private LocalDateTime expectedTime;
        private String event;
        private double probability;
        private String impact;
        private List<String> indicators;
    }
}
