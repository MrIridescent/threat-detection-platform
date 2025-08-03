package com.mriridescent.iris.service;

import com.mriridescent.iris.model.dto.EmailAnalysisDTO;
import com.mriridescent.iris.model.dto.EmailSubmissionDTO;

import java.util.List;

/**
 * Service interface for email analysis operations.
 */
public interface EmailAnalysisService {

    /**
     * Analyzes an email for potential threats.
     *
     * @param submission The email submission data.
     * @return The email analysis result.
     */
    EmailAnalysisDTO analyzeEmail(EmailSubmissionDTO submission);

    /**
     * Retrieves an email analysis by ID.
     *
     * @param id The email analysis ID.
     * @return The email analysis.
     */
    EmailAnalysisDTO getAnalysisById(Long id);

    /**
     * Retrieves all email analyses.
     *
     * @return A list of all email analyses.
     */
    List<EmailAnalysisDTO> getAllAnalyses();

    /**
     * Marks an email analysis as a false positive.
     *
     * @param id The email analysis ID.
     * @return The updated email analysis.
     */
    EmailAnalysisDTO markAsFalsePositive(Long id);

    /**
     * Retrieves analyses by threat level.
     *
     * @param threatLevel The threat level to filter by.
     * @return A list of email analyses with the specified threat level.
     */
    List<EmailAnalysisDTO> getAnalysesByThreatLevel(String threatLevel);
}
