package com.mriridescent.threatdetection.phishnet.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO representing a similar campaign match found by AI analysis
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimilarCampaignMatch {
    
    private Long campaignId;
    private String campaignName;
    
    /**
     * Similarity score (0.0 - 1.0) calculated using cosine similarity
     */
    private double similarityScore;
    
    /**
     * Shared tactics and techniques between campaigns
     */
    private List<String> sharedTactics;
    
    /**
     * Detailed feature comparison
     */
    private Map<String, FeatureComparison> featureComparisons;
    
    /**
     * Common infrastructure patterns
     */
    private List<InfrastructurePattern> commonPatterns;
    
    /**
     * Attribution analysis - potential same threat actor
     */
    private AttributionSimilarity attributionSimilarity;
    
    /**
     * Timeline correlation analysis
     */
    private TimelineCorrelation timelineCorrelation;
    
    /**
     * Confidence level for the match
     */
    private ConfidenceLevel confidenceLevel;
    
    /**
     * Recommended actions based on this similarity
     */
    private List<String> recommendedActions;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeatureComparison {
        private double sourceValue;
        private double targetValue;
        private double similarity;
        private String interpretation;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InfrastructurePattern {
        private String patternType;
        private String description;
        private double matchStrength;
        private List<String> commonElements;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttributionSimilarity {
        private double attributionScore; // 0.0 - 1.0
        private List<String> commonIndicators;
        private String likelyAttribution;
        private double confidence;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimelineCorrelation {
        private boolean overlappingTimeframes;
        private LocalDateTime earliestCommonActivity;
        private LocalDateTime latestCommonActivity;
        private String correlationPattern;
        private double temporalSimilarity;
    }
    
    public enum ConfidenceLevel {
        VERY_HIGH(0.9, "Very High - Strong evidence of correlation"),
        HIGH(0.7, "High - Significant similarities detected"),
        MEDIUM(0.5, "Medium - Notable patterns but requires validation"),
        LOW(0.3, "Low - Some similarities but may be coincidental"),
        VERY_LOW(0.1, "Very Low - Minimal correlation detected");
        
        private final double threshold;
        private final String description;
        
        ConfidenceLevel(double threshold, String description) {
            this.threshold = threshold;
            this.description = description;
        }
        
        public static ConfidenceLevel fromScore(double score) {
            if (score >= VERY_HIGH.threshold) return VERY_HIGH;
            if (score >= HIGH.threshold) return HIGH;
            if (score >= MEDIUM.threshold) return MEDIUM;
            if (score >= LOW.threshold) return LOW;
            return VERY_LOW;
        }
        
        public double getThreshold() { return threshold; }
        public String getDescription() { return description; }
    }
}
