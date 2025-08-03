package com.mriridescent.threatdetection.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents data used for machine learning model training.
 */
@Data
public class LearningData {
    private String id;
    private String modelType;
    private Map<String, Object> features;
    private Object label;
    private boolean isPriority;
    private String source;
    private LocalDateTime collectionTime;
    private boolean isLabeled;
}
