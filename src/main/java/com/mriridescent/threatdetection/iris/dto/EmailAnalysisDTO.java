package com.mriridescent.threatdetection.iris.dto;

import com.mriridescent.threatdetection.iris.model.entity.EmailAnalysis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
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
    private Map<String, String> metadata = new HashMap<>();
    private EmailAnalysis.ThreatLevel threatLevel;
    private double threatScore;
    private String analysisDetails;
    private boolean falsePositive;
    private LocalDateTime analyzedAt;
    private LocalDateTime updatedAt;
}
