package com.mriridescent.iris.repository;

import com.mriridescent.iris.model.entity.EmailAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for EmailAnalysis entities.
 */
@Repository
public interface EmailAnalysisRepository extends JpaRepository<EmailAnalysis, Long> {

    /**
     * Finds analyses by threat level.
     *
     * @param threatLevel The threat level to filter by.
     * @return A list of email analyses with the specified threat level.
     */
    List<EmailAnalysis> findByThreatLevel(EmailAnalysis.ThreatLevel threatLevel);

    /**
     * Finds analyses by sender.
     *
     * @param sender The sender email to filter by.
     * @return A list of email analyses from the specified sender.
     */
    List<EmailAnalysis> findBySenderContainingIgnoreCase(String sender);

    /**
     * Finds analyses by recipient.
     *
     * @param recipient The recipient email to filter by.
     * @return A list of email analyses to the specified recipient.
     */
    List<EmailAnalysis> findByRecipientContainingIgnoreCase(String recipient);

    /**
     * Finds analyses by subject.
     *
     * @param subject The subject to filter by.
     * @return A list of email analyses with the specified subject.
     */
    List<EmailAnalysis> findBySubjectContainingIgnoreCase(String subject);

    /**
     * Finds analyses by threat score range.
     *
     * @param minScore The minimum threat score.
     * @param maxScore The maximum threat score.
     * @return A list of email analyses within the specified threat score range.
     */
    List<EmailAnalysis> findByThreatScoreBetween(double minScore, double maxScore);

    /**
     * Finds analyses by false positive flag.
     *
     * @param falsePositive The false positive flag to filter by.
     * @return A list of email analyses with the specified false positive flag.
     */
    List<EmailAnalysis> findByFalsePositive(boolean falsePositive);
}
