package com.mriridescent.phishnet.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * DTO for infrastructure node data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfrastructureNodeDTO {

    private Long id;
    private String identifier;
    private String nodeType;
    private String description;
    private String status;
    private Set<String> attributes;
    private List<Long> connectedToIds;
    private boolean isMalicious;
    private LocalDateTime discoveredAt;
    private LocalDateTime lastSeenAt;
}
