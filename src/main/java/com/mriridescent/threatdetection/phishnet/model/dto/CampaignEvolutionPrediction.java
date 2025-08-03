package com.mriridescent.threatdetection.phishnet.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for AI-powered campaign evolution predictions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignEvolutionPrediction {
    
    private Long campaignId;
    private String campaignName;
    
    /**
     * Likelihood that the campaign will evolve/expand (0.0 - 1.0)
     */
    private double evolutionLikelihood;
    
    /**
     * Score indicating potential threat escalation (0.0 - 1.0)
     */
    private double threatEscalationScore;
    
    /**
     * AI-predicted next actions the campaign might take
     */
    private List<String> predictedNextActions;
    
    /**
     * Confidence score for the prediction (0.0 - 1.0)
     */
    private double confidenceScore;
    
    /**
     * Predicted timeframe for evolution (in hours)
     */
    private Integer predictedTimeframeHours;
    
    /**
     * Risk factors identified by AI analysis
     */
    private List<RiskFactor> riskFactors;
    
    /**
     * Recommended monitoring priorities
     */
    private List<MonitoringPriority> monitoringPriorities;
    
    /**
     * When this prediction was generated
     */
    private LocalDateTime predictionTimestamp;
    
    /**
     * Model performance metrics for this prediction
     */
    private ModelPerformanceMetrics performanceMetrics;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RiskFactor {
        private String factor;
        private double severity; // 0.0 - 1.0
        private String description;
        private List<String> indicators;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonitoringPriority {
        private String area;
        private String priority; // HIGH, MEDIUM, LOW
        private String description;
        private List<String> specificIndicators;
        private Integer checkIntervalMinutes;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelPerformanceMetrics {
        private double accuracy;
        private double precision;
        private double recall;
        private double f1Score;
        private String modelVersion;
        private LocalDateTime lastTrainedAt;
    }
}
