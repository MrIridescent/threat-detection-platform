package com.mriridescent.threatdetection.iris.controller;

import com.mriridescent.threatdetection.controller.ApiResponse;
import com.mriridescent.threatdetection.iris.dto.MLModelDto;
import com.mriridescent.threatdetection.iris.model.entity.MLModel;
import com.mriridescent.threatdetection.iris.service.MLModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for ML model management operations.
 */
@RestController
@RequestMapping("/api/v1/iris/models")
@RequiredArgsConstructor
@Tag(name = "ML Models", description = "ML model management operations")
public class MLModelController {

    private final MLModelService mlModelService;

    /**
     * Get all ML models
     *
     * @return List of all models
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(summary = "Get all ML models", description = "Retrieves all machine learning models")
    public ResponseEntity<ApiResponse<List<MLModelDto>>> getAllModels() {
        List<MLModelDto> models = mlModelService.getAllModels().stream()
                .map(MLModelDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Models retrieved successfully", models));
    }

    /**
     * Get active ML models
     *
     * @return List of active models
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(summary = "Get active ML models", description = "Retrieves all active machine learning models")
    public ResponseEntity<ApiResponse<List<MLModelDto>>> getActiveModels() {
        List<MLModelDto> models = mlModelService.getActiveModels().stream()
                .map(MLModelDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Active models retrieved successfully", models));
    }

    /**
     * Get ML model by ID
     *
     * @param id Model ID
     * @return Model details
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(summary = "Get ML model by ID", description = "Retrieves a specific machine learning model by its ID")
    public ResponseEntity<ApiResponse<MLModelDto>> getModelById(@PathVariable Long id) {
        return mlModelService.getModelById(id)
                .map(model -> ResponseEntity.ok(ApiResponse.success(
                        "Model retrieved successfully", 
                        MLModelDto.fromEntity(model))))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Model not found with id: " + id)));
    }

    /**
     * Create a new ML model
     *
     * @param modelDto Model details
     * @return Created model
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new ML model", description = "Creates a new machine learning model")
    public ResponseEntity<ApiResponse<MLModelDto>> createModel(@Valid @RequestBody MLModelDto modelDto) {
        MLModel savedModel = mlModelService.createModel(modelDto.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Model created successfully", MLModelDto.fromEntity(savedModel)));
    }

    /**
     * Update an existing ML model
     *
     * @param id Model ID
     * @param modelDto Updated model details
     * @return Updated model
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an ML model", description = "Updates an existing machine learning model")
    public ResponseEntity<ApiResponse<MLModelDto>> updateModel(
            @PathVariable Long id, 
            @Valid @RequestBody MLModelDto modelDto) {
        MLModel updatedModel = mlModelService.updateModel(id, modelDto.toEntity());
        return ResponseEntity.ok(ApiResponse.success("Model updated successfully", MLModelDto.fromEntity(updatedModel)));
    }

    /**
     * Delete an ML model
     *
     * @param id Model ID
     * @return Success response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete an ML model", description = "Deletes an existing machine learning model")
    public ResponseEntity<ApiResponse<Void>> deleteModel(@PathVariable Long id) {
        mlModelService.deleteModel(id);
        return ResponseEntity.ok(ApiResponse.success("Model deleted successfully"));
    }

    /**
     * Activate an ML model
     *
     * @param id Model ID
     * @return Activated model
     */
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate an ML model", description = "Activates a machine learning model")
    public ResponseEntity<ApiResponse<MLModelDto>> activateModel(@PathVariable Long id) {
        MLModel activatedModel = mlModelService.activateModel(id);
        return ResponseEntity.ok(ApiResponse.success("Model activated successfully", MLModelDto.fromEntity(activatedModel)));
    }

    /**
     * Deactivate an ML model
     *
     * @param id Model ID
     * @return Deactivated model
     */
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate an ML model", description = "Deactivates a machine learning model")
    public ResponseEntity<ApiResponse<MLModelDto>> deactivateModel(@PathVariable Long id) {
        MLModel deactivatedModel = mlModelService.deactivateModel(id);
        return ResponseEntity.ok(ApiResponse.success("Model deactivated successfully", MLModelDto.fromEntity(deactivatedModel)));
    }
}
