package com.mriridescent.threatdetection.phishnet.dto;

import com.mriridescent.threatdetection.phishnet.model.entity.PhishingCampaign;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for detailed phishing campaign information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignDetailDTO {

    private Long id;
    private String name;
    private String description;
    private PhishingCampaign.CampaignStatus status;
    private PhishingCampaign.ThreatLevel threatLevel;
    private String targetSector;
    private LocalDateTime discoveredAt;
    private LocalDateTime resolvedAt;
    private String createdByUsername;
    private String assignedToUsername;
    private String tacticsAndTechniques;
    private String mitigationStrategy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<InfrastructureNodeDTO> infrastructureNodes;
    private List<AttackVectorDTO> attackVectors;
}
