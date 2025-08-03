package com.mriridescent.threatdetection.phishnet.dto;

import com.mriridescent.threatdetection.phishnet.model.entity.AttackVector;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * DTO for attack vector data transfer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttackVectorDto {

    private Long id;

    private Long campaignId;

    @NotBlank(message = "Vector name is required")
    private String name;

    @NotNull(message = "Vector type is required")
    private AttackVector.VectorType vectorType;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Status is required")
    private AttackVector.VectorStatus status;

    @Min(value = 1, message = "Prevalence must be between 1 and 10")
    @Max(value = 10, message = "Prevalence must be between 1 and 10")
    private int prevalence;

    @Min(value = 1, message = "Effectiveness must be between 1 and 10")
    @Max(value = 10, message = "Effectiveness must be between 1 and 10")
    private int effectiveness;

    @NotBlank(message = "Technical details are required")
    private String technicalDetails;

    private String mitigationSteps;

    private LocalDateTime identifiedAt;

    private LocalDateTime lastObservedAt;

    /**
     * Convert DTO to entity
     *
     * @return AttackVector entity
     */
    public AttackVector toEntity() {
        return AttackVector.builder()
                .id(id)
                .name(name)
                .vectorType(vectorType)
                .description(description)
                .status(status)
                .prevalence(prevalence)
                .effectiveness(effectiveness)
                .technicalDetails(technicalDetails)
                .mitigationSteps(mitigationSteps)
                .identifiedAt(identifiedAt != null ? identifiedAt : LocalDateTime.now())
                .lastObservedAt(lastObservedAt != null ? lastObservedAt : LocalDateTime.now())
                .build();
    }

    /**
     * Create DTO from entity
     *
     * @param vector Attack vector entity
     * @return AttackVectorDto
     */
    public static AttackVectorDto fromEntity(AttackVector vector) {
        return AttackVectorDto.builder()
                .id(vector.getId())
                .campaignId(vector.getCampaign() != null ? vector.getCampaign().getId() : null)
                .name(vector.getName())
                .vectorType(vector.getVectorType())
                .description(vector.getDescription())
                .status(vector.getStatus())
                .prevalence(vector.getPrevalence())
                .effectiveness(vector.getEffectiveness())
                .technicalDetails(vector.getTechnicalDetails())
                .mitigationSteps(vector.getMitigationSteps())
                .identifiedAt(vector.getIdentifiedAt())
                .lastObservedAt(vector.getLastObservedAt())
                .build();
    }
}
