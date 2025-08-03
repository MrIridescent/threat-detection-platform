package com.mriridescent.threatdetection.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data structure for pattern learning and model improvement.
 */
@Data
@Builder
public class PatternLearningData {
    
    private NetworkPacket networkPacket;
    private ThreatAlert threatAlert;
    private LocalDateTime timestamp;
    private String feedbackType;
    private boolean falsePositive;
    private String analystNotes;
    private double confidenceScore;
    
    /**
     * Check if this is feedback data.
     */
    public boolean isFeedback() {
        return feedbackType != null;
    }
    
    /**
     * Check if this represents a false positive.
     */
    public boolean isFalsePositive() {
        return falsePositive;
    }
    
    /**
     * Check if this is high-confidence data.
     */
    public boolean isHighConfidence() {
        return confidenceScore >= 0.8;
    }
    
    /**
     * Get the learning priority based on confidence and feedback.
     */
    public int getLearningPriority() {
        if (isFalsePositive()) {
            return 10; // High priority for false positives
        } else if (isHighConfidence()) {
            return 8;  // High priority for confident detections
        } else if (isFeedback()) {
            return 6;  // Medium priority for analyst feedback
        } else {
            return 3;  // Low priority for regular detections
        }
    }
}
