package com.mriridescent.threatdetection.iris.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO for email analysis results.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResultDto {

    private Long id;

    private String emailSubject;

    private String emailSender;

    private double threatScore;

    private String threatLevel;

    private List<String> detectedThreats;

    private Map<String, Double> modelScores;

    private List<String> flaggedElements;

    private Map<String, String> metadataAnalysis;

    private Map<String, String> headerAnalysis;

    private List<String> recommendedActions;

    private boolean falsePositiveFeedback;

    private LocalDateTime analyzedAt;

    private String analyzedBy;
}
