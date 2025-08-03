package com.mriridescent.threatdetection.phishnet.dto;

import com.mriridescent.threatdetection.phishnet.model.entity.PhishingCampaign;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for phishing campaign information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignDTO {

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
    private int nodeCount;
    private int vectorCount;
    private String tacticsAndTechniques;
    private String mitigationStrategy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
