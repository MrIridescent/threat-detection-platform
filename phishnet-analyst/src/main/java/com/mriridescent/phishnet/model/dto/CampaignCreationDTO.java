package com.mriridescent.phishnet.model.dto;

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

    private String name;
    private String description;
    private String threatLevel;
    private String targetSector;
    private String tacticsAndTechniques;
    private String mitigationStrategy;
}
