package com.mriridescent.threatdetection.iris.controller;

import com.mriridescent.threatdetection.iris.dto.EmailAnalysisDTO;
import com.mriridescent.threatdetection.iris.dto.EmailSubmissionDTO;
import com.mriridescent.threatdetection.iris.mapper.EmailAnalysisMapper;
import com.mriridescent.threatdetection.iris.model.entity.EmailAnalysis;
import com.mriridescent.threatdetection.iris.service.EmailAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for email analysis operations in Project Iris.
 */
@RestController
@RequestMapping("/api/v1/iris")
@RequiredArgsConstructor
@Tag(name = "Email Analysis", description = "API for analyzing emails for phishing threats")
public class EmailAnalysisController {

    private final EmailAnalysisService emailAnalysisService;
    private final EmailAnalysisMapper emailAnalysisMapper;

    @PostMapping("/analyze")
    @Operation(summary = "Analyze an email for threats", 
              description = "Submits an email for analysis by the Iris threat detection engine")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<EmailAnalysisDTO> analyzeEmail(@Valid @RequestBody EmailSubmissionDTO submission) {
        EmailAnalysis analysis = emailAnalysisService.analyzeEmail(
                submission.getEmailId(),
                submission.getSender(),
                submission.getRecipient(),
                submission.getSubject(),
                submission.getContent(),
                submission.getMetadata()
        );

        return ResponseEntity.ok(emailAnalysisMapper.toDto(analysis));
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent analyses", 
              description = "Retrieves the most recent email analyses")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<List<EmailAnalysisDTO>> getRecentAnalyses(
            @RequestParam(defaultValue = "10") int limit) {
        List<EmailAnalysis> analyses = emailAnalysisService.getRecentAnalyses(limit);
        List<EmailAnalysisDTO> dtos = analyses.stream()
                .map(emailAnalysisMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/threats")
    @Operation(summary = "Get high threat analyses", 
              description = "Retrieves email analyses with high threat levels")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<List<EmailAnalysisDTO>> getHighThreatAnalyses() {
        List<EmailAnalysis> analyses = emailAnalysisService.getHighThreatAnalyses();
        List<EmailAnalysisDTO> dtos = analyses.stream()
                .map(emailAnalysisMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{id}/false-positive")
    @Operation(summary = "Mark as false positive", 
              description = "Marks an analysis as a false positive for model improvement")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<EmailAnalysisDTO> markAsFalsePositive(@PathVariable Long id) {
        EmailAnalysis analysis = emailAnalysisService.markAsFalsePositive(id);
        return ResponseEntity.ok(emailAnalysisMapper.toDto(analysis));
    }
}
