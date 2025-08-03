package com.mriridescent.threatdetection.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents a machine learning model update.
 */
@Data
public class ModelUpdate {
    private String modelType;
    private LocalDateTime updateTime;
    private int trainingDataSize;
    private Map<String, Double> performanceMetrics;
    private String modelVersion;
    private String updateStatus;
}
