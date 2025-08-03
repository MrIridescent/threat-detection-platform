package com.mriridescent.threatdetection.iris.controller;

import com.mriridescent.threatdetection.controller.ApiResponse;
import com.mriridescent.threatdetection.iris.dto.AnalysisRequestDto;
import com.mriridescent.threatdetection.iris.dto.AnalysisResultDto;
import com.mriridescent.threatdetection.iris.service.IrisAnalysisService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Iris threat analysis operations.
 */
@RestController
@RequestMapping("/api/v1/iris/analysis")
@RequiredArgsConstructor
@Tag(name = "Iris Analysis", description = "Iris threat analysis operations")
public class IrisAnalysisController {

    private final IrisAnalysisService analysisService;

    /**
     * Analyze an email for threats
     *
     * @param request Analysis request
     * @return Analysis result
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST', 'USER')")
    @RateLimiter(name = "irisAnalysisLimiter")
    @Operation(summary = "Analyze email for threats", description = "Analyzes an email for potential threats using ML models")
    public ResponseEntity<ApiResponse<AnalysisResultDto>> analyzeEmail(@Valid @RequestBody AnalysisRequestDto request) {
        AnalysisResultDto result = analysisService.analyzeEmail(request);
        return ResponseEntity.ok(ApiResponse.success("Email analyzed successfully", result));
    }

    /**
     * Get recent analyses
     *
     * @param limit Maximum number of results to return
     * @return List of recent analysis results
     */
    @GetMapping("/recent")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(summary = "Get recent analyses", description = "Retrieves recent email analysis results")
    public ResponseEntity<ApiResponse<List<AnalysisResultDto>>> getRecentAnalyses(@RequestParam(defaultValue = "10") int limit) {
        List<AnalysisResultDto> results = analysisService.getRecentAnalyses(limit);
        return ResponseEntity.ok(ApiResponse.success("Recent analyses retrieved successfully", results));
    }

    /**
     * Get analyses by threat level
     *
     * @param minThreatLevel Minimum threat level (0-1)
     * @param limit Maximum number of results to return
     * @return List of high-threat analysis results
     */
    @GetMapping("/threats")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(summary = "Get high-threat analyses", description = "Retrieves email analyses with high threat scores")
    public ResponseEntity<ApiResponse<List<AnalysisResultDto>>> getHighThreatAnalyses(
            @RequestParam(defaultValue = "0.75") double minThreatLevel,
            @RequestParam(defaultValue = "10") int limit) {
        List<AnalysisResultDto> results = analysisService.getAnalysesByMinThreatLevel(minThreatLevel, limit);
        return ResponseEntity.ok(ApiResponse.success("High threat analyses retrieved successfully", results));
    }

    /**
     * Submit feedback on analysis result
     *
     * @param analysisId Analysis ID
     * @param isThreat Whether the analysis correctly identified a threat
     * @return Updated analysis result
     */
    @PostMapping("/{analysisId}/feedback")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(summary = "Submit feedback on analysis", description = "Submits feedback on an analysis result to improve model accuracy")
    public ResponseEntity<ApiResponse<AnalysisResultDto>> submitFeedback(
            @PathVariable Long analysisId,
            @RequestParam boolean isThreat) {
        AnalysisResultDto updatedResult = analysisService.submitFeedback(analysisId, isThreat);
        return ResponseEntity.ok(ApiResponse.success("Feedback submitted successfully", updatedResult));
    }
}
