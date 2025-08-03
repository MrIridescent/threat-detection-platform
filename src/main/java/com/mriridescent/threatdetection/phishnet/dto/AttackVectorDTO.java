package com.mriridescent.threatdetection.phishnet.dto;

import com.mriridescent.threatdetection.phishnet.model.entity.AttackVector;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for attack vector information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttackVectorDTO {

    private Long id;
    private Long campaignId;
    private String name;
    private AttackVector.VectorType vectorType;
    private String description;
    private AttackVector.VectorStatus status;
    private int prevalence;
    private int effectiveness;
    private String technicalDetails;
    private String mitigationSteps;
    private LocalDateTime identifiedAt;
    private LocalDateTime lastObservedAt;
}
