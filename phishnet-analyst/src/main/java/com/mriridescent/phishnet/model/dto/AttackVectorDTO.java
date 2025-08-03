package com.mriridescent.phishnet.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for attack vector data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttackVectorDTO {

    private Long id;
    private String name;
    private String vectorType;
    private String description;
    private String status;
    private int prevalence;
    private int effectiveness;
    private String technicalDetails;
    private String mitigationSteps;
    private LocalDateTime identifiedAt;
    private LocalDateTime lastObservedAt;
}
