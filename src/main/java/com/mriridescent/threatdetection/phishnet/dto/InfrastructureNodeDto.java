package com.mriridescent.threatdetection.phishnet.dto;

import com.mriridescent.threatdetection.phishnet.model.entity.InfrastructureNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * DTO for infrastructure node data transfer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfrastructureNodeDto {

    private Long id;

    private Long campaignId;

    @NotBlank(message = "Node name is required")
    private String name;

    @NotNull(message = "Node type is required")
    private InfrastructureNode.NodeType nodeType;

    @NotBlank(message = "Identifier is required")
    private String identifier;

    @NotNull(message = "Status is required")
    private InfrastructureNode.NodeStatus status;

    @NotBlank(message = "Technical details are required")
    private String technicalDetails;

    private String mitigationSteps;

    private LocalDateTime discoveredAt;

    private LocalDateTime lastCheckedAt;

    /**
     * Convert DTO to entity
     *
     * @return InfrastructureNode entity
     */
    public InfrastructureNode toEntity() {
        return InfrastructureNode.builder()
                .id(id)
                .name(name)
                .nodeType(nodeType)
                .identifier(identifier)
                .status(status)
                .technicalDetails(technicalDetails)
                .mitigationSteps(mitigationSteps)
                .discoveredAt(discoveredAt != null ? discoveredAt : LocalDateTime.now())
                .lastCheckedAt(lastCheckedAt != null ? lastCheckedAt : LocalDateTime.now())
                .build();
    }

    /**
     * Create DTO from entity
     *
     * @param node Infrastructure node entity
     * @return InfrastructureNodeDto
     */
    public static InfrastructureNodeDto fromEntity(InfrastructureNode node) {
        return InfrastructureNodeDto.builder()
                .id(node.getId())
                .campaignId(node.getCampaign() != null ? node.getCampaign().getId() : null)
                .name(node.getName())
                .nodeType(node.getNodeType())
                .identifier(node.getIdentifier())
                .status(node.getStatus())
                .technicalDetails(node.getTechnicalDetails())
                .mitigationSteps(node.getMitigationSteps())
                .discoveredAt(node.getDiscoveredAt())
                .lastCheckedAt(node.getLastCheckedAt())
                .build();
    }
}
