package com.mriridescent.iris.model.dto;

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
public class EmailAnalysisDTO {

    private Long id;
    private String emailId;
    private String sender;
    private String recipient;
    private String subject;
    private String content;
    private Map<String, String> metadata;
    private String threatLevel;
    private double threatScore;
    private String analysisDetails;
    private String analyzedBy;
    private LocalDateTime analyzedAt;
    private LocalDateTime updatedAt;
    private boolean falsePositive;
    private List<EmailFeatureDTO> features;
}
