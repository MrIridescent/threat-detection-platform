package com.mriridescent.threatdetection.iris.dto;

import com.mriridescent.threatdetection.iris.model.entity.MLModel;
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
 * DTO for ML model data transfer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MLModelDto {

    private Long id;

    @NotBlank(message = "Model name is required")
    private String name;

    @NotBlank(message = "Model version is required")
    private String version;

    @NotNull(message = "Model type is required")
    private MLModel.ModelType modelType;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "File path is required")
    private String filePath;

    @Min(value = 0, message = "Accuracy must be between 0 and 1")
    @Max(value = 1, message = "Accuracy must be between 0 and 1")
    private double accuracy;

    @Min(value = 0, message = "Precision must be between 0 and 1")
    @Max(value = 1, message = "Precision must be between 0 and 1")
    private double precision;

    @Min(value = 0, message = "Recall must be between 0 and 1")
    @Max(value = 1, message = "Recall must be between 0 and 1")
    private double recall;

    @Min(value = 0, message = "F1 score must be between 0 and 1")
    @Max(value = 1, message = "F1 score must be between 0 and 1")
    private double f1Score;

    private String createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime lastTrainedAt;

    private boolean active;

    /**
     * Convert DTO to entity
     *
     * @return MLModel entity
     */
    public MLModel toEntity() {
        return MLModel.builder()
                .id(id)
                .name(name)
                .version(version)
                .modelType(modelType)
                .description(description)
                .filePath(filePath)
                .accuracy(accuracy)
                .precision(precision)
                .recall(recall)
                .f1Score(f1Score)
                .createdAt(createdAt)
                .lastTrainedAt(lastTrainedAt)
                .active(active)
                .build();
    }

    /**
     * Create DTO from entity
     *
     * @param model ML model entity
     * @return MLModelDto
     */
    public static MLModelDto fromEntity(MLModel model) {
        return MLModelDto.builder()
                .id(model.getId())
                .name(model.getName())
                .version(model.getVersion())
                .modelType(model.getModelType())
                .description(model.getDescription())
                .filePath(model.getFilePath())
                .accuracy(model.getAccuracy())
                .precision(model.getPrecision())
                .recall(model.getRecall())
                .f1Score(model.getF1Score())
                .createdBy(model.getCreatedBy() != null ? model.getCreatedBy().getUsername() : null)
                .createdAt(model.getCreatedAt())
                .lastTrainedAt(model.getLastTrainedAt())
                .active(model.isActive())
                .build();
    }
}
