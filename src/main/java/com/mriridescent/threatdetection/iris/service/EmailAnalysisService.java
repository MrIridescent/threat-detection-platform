package com.mriridescent.threatdetection.iris.service;

import com.mriridescent.threatdetection.iris.model.entity.EmailAnalysis;
import com.mriridescent.threatdetection.iris.model.entity.EmailFeature;
import com.mriridescent.threatdetection.iris.model.entity.MLModel;
import com.mriridescent.threatdetection.iris.repository.EmailAnalysisRepository;
import com.mriridescent.threatdetection.iris.repository.EmailFeatureRepository;
import com.mriridescent.threatdetection.phishnet.service.PhishingCampaignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Service for analyzing emails and detecting phishing threats.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailAnalysisService {

    private final EmailAnalysisRepository emailAnalysisRepository;
    private final EmailFeatureRepository emailFeatureRepository;
    private final MLModelService mlModelService;
    private final FeatureExtractionService featureExtractionService;
    private final PhishingCampaignService phishingCampaignService;

    /**
     * Analyzes an email for phishing threats.
     *
     * @param sender    The email sender
     * @param recipient The email recipient
     * @param subject   The email subject
     * @param content   The email content
     * @param metadata  Additional email metadata
     * @return The analysis result
     */
    @Transactional
    public EmailAnalysis analyzeEmail(String emailId, String sender, String recipient, 
                                      String subject, String content, Map<String, String> metadata) {
        log.info("Analyzing email from {} to {}", sender, recipient);

        // Extract features from the email
        List<EmailFeature> features = featureExtractionService.extractFeatures(sender, recipient, subject, content, metadata);

        // Get the active ML model
        MLModel activeModel = mlModelService.getActiveModel();

        // Prepare data for model prediction
        double threatScore = mlModelService.predictThreatScore(activeModel, features);
        EmailAnalysis.ThreatLevel threatLevel = determineThreatLevel(threatScore);

        // Create and save the analysis
        EmailAnalysis analysis = EmailAnalysis.builder()
                .emailId(emailId)
                .sender(sender)
                .recipient(recipient)
                .subject(subject)
                .content(content)
                .metadata(metadata)
                .threatScore(threatScore)
                .threatLevel(threatLevel)
                .analysisDetails(generateAnalysisDetails(features, threatScore))
                .build();

        EmailAnalysis savedAnalysis = emailAnalysisRepository.save(analysis);

        // Associate features with the saved analysis
        features.forEach(feature -> {
            feature.setEmailAnalysis(savedAnalysis);
            emailFeatureRepository.save(feature);
        });

        // If the threat is significant, check for potential campaign connection
        if (threatLevel == EmailAnalysis.ThreatLevel.DANGEROUS || 
            threatLevel == EmailAnalysis.ThreatLevel.CRITICAL) {
            phishingCampaignService.checkForCampaignAssociation(savedAnalysis);
        }

        log.info("Email analysis completed. Threat level: {}, Score: {}", threatLevel, threatScore);
        return savedAnalysis;
    }

    /**
     * Marks an analysis as a false positive, updating the ML model.
     *
     * @param analysisId The ID of the analysis to mark
     * @return The updated analysis
     */
    @Transactional
    public EmailAnalysis markAsFalsePositive(Long analysisId) {
        EmailAnalysis analysis = emailAnalysisRepository.findById(analysisId)
                .orElseThrow(() -> new IllegalArgumentException("Analysis not found"));

        analysis.setFalsePositive(true);
        EmailAnalysis savedAnalysis = emailAnalysisRepository.save(analysis);

        // Update the model with this feedback
        mlModelService.updateModelWithFeedback(savedAnalysis);

        return savedAnalysis;
    }

    /**
     * Gets a list of recent analyses.
     *
     * @param limit The maximum number of analyses to return
     * @return List of recent email analyses
     */
    public List<EmailAnalysis> getRecentAnalyses(int limit) {
        return emailAnalysisRepository.findTopByOrderByAnalyzedAtDesc(limit);
    }

    /**
     * Gets analyses with high threat levels.
     *
     * @return List of dangerous or critical analyses
     */
    public List<EmailAnalysis> getHighThreatAnalyses() {
        return emailAnalysisRepository.findByThreatLevelIn(
                List.of(EmailAnalysis.ThreatLevel.DANGEROUS, EmailAnalysis.ThreatLevel.CRITICAL));
    }

    /**
     * Determines the threat level based on the threat score.
     *
     * @param threatScore The calculated threat score
     * @return The corresponding threat level
     */
    private EmailAnalysis.ThreatLevel determineThreatLevel(double threatScore) {
        if (threatScore < 0.3) {
            return EmailAnalysis.ThreatLevel.SAFE;
        } else if (threatScore < 0.6) {
            return EmailAnalysis.ThreatLevel.SUSPICIOUS;
        } else if (threatScore < 0.8) {
            return EmailAnalysis.ThreatLevel.DANGEROUS;
        } else {
            return EmailAnalysis.ThreatLevel.CRITICAL;
        }
    }

    /**
     * Generates detailed analysis text based on the features and score.
     *
     * @param features    The extracted features
     * @param threatScore The calculated threat score
     * @return Detailed analysis text
     */
    private String generateAnalysisDetails(List<EmailFeature> features, double threatScore) {
        StringBuilder details = new StringBuilder();
        details.append("Threat score: ").append(String.format("%.2f", threatScore)).append("\n\n");
        details.append("Key factors in analysis:\n");

        // Sort features by weight and include the most significant ones
        features.stream()
                .sorted((f1, f2) -> Double.compare(Math.abs(f2.getWeight()), Math.abs(f1.getWeight())))
                .limit(5)
                .forEach(feature -> {
                    details.append("- ").append(feature.getFeatureName()).append(": ")
                           .append(feature.getFeatureValue()).append(" (weight: ")
                           .append(String.format("%.2f", feature.getWeight())).append(")\n");
                });

        return details.toString();
    }
}
