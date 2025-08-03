package com.mriridescent.iris.controller;

import com.mriridescent.iris.model.dto.EmailAnalysisDTO;
import com.mriridescent.iris.model.dto.EmailSubmissionDTO;
import com.mriridescent.iris.service.EmailAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for email analysis operations.
 */
@RestController
@RequestMapping("/v1/analysis")
@RequiredArgsConstructor
public class EmailAnalysisController {

    private final EmailAnalysisService emailAnalysisService;

    /**
     * Submits an email for analysis.
     *
     * @param submission The email submission data.
     * @return The created email analysis.
     */
    @PostMapping("/submit")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<EmailAnalysisDTO> submitEmail(@RequestBody EmailSubmissionDTO submission) {
        return ResponseEntity.ok(emailAnalysisService.analyzeEmail(submission));
    }

    /**
     * Retrieves an email analysis by ID.
     *
     * @param id The email analysis ID.
     * @return The email analysis.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<EmailAnalysisDTO> getAnalysis(@PathVariable Long id) {
        return ResponseEntity.ok(emailAnalysisService.getAnalysisById(id));
    }

    /**
     * Retrieves all email analyses.
     *
     * @return A list of all email analyses.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<List<EmailAnalysisDTO>> getAllAnalyses() {
        return ResponseEntity.ok(emailAnalysisService.getAllAnalyses());
    }

    /**
     * Marks an email analysis as a false positive.
     *
     * @param id The email analysis ID.
     * @return The updated email analysis.
     */
    @PutMapping("/{id}/false-positive")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<EmailAnalysisDTO> markAsFalsePositive(@PathVariable Long id) {
        return ResponseEntity.ok(emailAnalysisService.markAsFalsePositive(id));
    }

    /**
     * Retrieves analyses by threat level.
     *
     * @param threatLevel The threat level to filter by.
     * @return A list of email analyses with the specified threat level.
     */
    @GetMapping("/threat-level/{threatLevel}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<List<EmailAnalysisDTO>> getAnalysesByThreatLevel(
            @PathVariable String threatLevel) {
        return ResponseEntity.ok(emailAnalysisService.getAnalysesByThreatLevel(threatLevel));
    }
}
