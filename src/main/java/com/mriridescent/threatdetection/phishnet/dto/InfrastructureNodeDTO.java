package com.mriridescent.threatdetection.phishnet.dto;

import com.mriridescent.threatdetection.phishnet.model.entity.InfrastructureNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * DTO for infrastructure node information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfrastructureNodeDTO {

    private Long id;
    private Long campaignId;
    private String identifier;
    private InfrastructureNode.NodeType nodeType;
    private String description;
    private InfrastructureNode.NodeStatus status;
    private Set<String> attributes;
    private List<Long> connectedToIds;
    private boolean isMalicious;
    private LocalDateTime discoveredAt;
    private LocalDateTime lastSeenAt;
}
