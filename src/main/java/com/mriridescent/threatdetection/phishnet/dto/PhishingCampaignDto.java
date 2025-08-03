package com.mriridescent.threatdetection.phishnet.dto;

import com.mriridescent.threatdetection.phishnet.model.entity.PhishingCampaign;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for phishing campaign data transfer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhishingCampaignDto {

    private Long id;

    @NotBlank(message = "Campaign name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Status is required")
    private PhishingCampaign.CampaignStatus status;

    @NotNull(message = "Severity is required")
    private PhishingCampaign.Severity severity;

    @NotBlank(message = "Target demographic is required")
    private String targetDemographic;

    @Min(value = 0, message = "Estimated reach must be a positive number")
    private int estimatedReach;

    @NotBlank(message = "Origin region is required")
    private String originRegion;

    @NotBlank(message = "Technical details are required")
    private String technicalDetails;

    private String createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdatedAt;

    private LocalDateTime firstObservedAt;

    private LocalDateTime lastObservedAt;

    private List<InfrastructureNodeDto> infrastructureNodes;

    private List<AttackVectorDto> attackVectors;

    /**
     * Convert DTO to entity
     *
     * @return PhishingCampaign entity
     */
    public PhishingCampaign toEntity() {
        return PhishingCampaign.builder()
                .id(id)
                .name(name)
                .description(description)
                .status(status)
                .severity(severity)
                .targetDemographic(targetDemographic)
                .estimatedReach(estimatedReach)
                .originRegion(originRegion)
                .technicalDetails(technicalDetails)
                .createdAt(createdAt != null ? createdAt : LocalDateTime.now())
                .lastUpdatedAt(lastUpdatedAt != null ? lastUpdatedAt : LocalDateTime.now())
                .firstObservedAt(firstObservedAt)
                .lastObservedAt(lastObservedAt)
                .build();
    }

    /**
     * Create DTO from entity
     *
     * @param campaign Phishing campaign entity
     * @return PhishingCampaignDto
     */
    public static PhishingCampaignDto fromEntity(PhishingCampaign campaign) {
        return PhishingCampaignDto.builder()
                .id(campaign.getId())
                .name(campaign.getName())
                .description(campaign.getDescription())
                .status(campaign.getStatus())
                .severity(campaign.getSeverity())
                .targetDemographic(campaign.getTargetDemographic())
                .estimatedReach(campaign.getEstimatedReach())
                .originRegion(campaign.getOriginRegion())
                .technicalDetails(campaign.getTechnicalDetails())
                .createdBy(campaign.getCreatedBy() != null ? campaign.getCreatedBy().getUsername() : null)
                .createdAt(campaign.getCreatedAt())
                .lastUpdatedAt(campaign.getLastUpdatedAt())
                .firstObservedAt(campaign.getFirstObservedAt())
                .lastObservedAt(campaign.getLastObservedAt())
                .infrastructureNodes(campaign.getInfrastructureNodes() != null ?
                        campaign.getInfrastructureNodes().stream()
                                .map(InfrastructureNodeDto::fromEntity)
                                .collect(Collectors.toList()) : null)
                .attackVectors(campaign.getAttackVectors() != null ?
                        campaign.getAttackVectors().stream()
                                .map(AttackVectorDto::fromEntity)
                                .collect(Collectors.toList()) : null)
                .build();
    }
}
