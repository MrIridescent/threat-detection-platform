package com.mriridescent.phishnet.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for phishing campaign data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhishingCampaignDTO {

    private Long id;
    private String name;
    private String description;
    private String status;
    private String threatLevel;
    private String targetSector;
    private LocalDateTime discoveredAt;
    private LocalDateTime resolvedAt;
    private String createdBy;
    private String assignedTo;
    private List<InfrastructureNodeDTO> infrastructureNodes;
    private List<AttackVectorDTO> attackVectors;
    private String tacticsAndTechniques;
    private String mitigationStrategy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
