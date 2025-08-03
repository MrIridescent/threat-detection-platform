package com.mriridescent.iris.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for email feature data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailFeatureDTO {

    private Long id;
    private String featureName;
    private String featureValue;
    private double weight;
    private String featureType;
    private LocalDateTime extractedAt;
}
