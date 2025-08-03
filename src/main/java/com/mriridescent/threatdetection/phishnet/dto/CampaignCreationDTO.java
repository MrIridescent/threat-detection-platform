package com.mriridescent.threatdetection.phishnet.dto;

import com.mriridescent.threatdetection.phishnet.model.entity.PhishingCampaign;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new phishing campaign.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignCreationDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Threat level is required")
    private PhishingCampaign.ThreatLevel threatLevel;

    @NotBlank(message = "Target sector is required")
    private String targetSector;

    @NotBlank(message = "Tactics and techniques are required")
    private String tacticsAndTechniques;
}
