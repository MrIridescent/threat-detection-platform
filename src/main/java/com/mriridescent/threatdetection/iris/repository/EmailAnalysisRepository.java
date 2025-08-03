package com.mriridescent.threatdetection.iris.repository;

import com.mriridescent.threatdetection.iris.model.entity.EmailAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for EmailAnalysis entities.
 */
@Repository
public interface EmailAnalysisRepository extends JpaRepository<EmailAnalysis, Long> {

    /**
     * Finds the most recent email analyses.
     *
     * @param limit Maximum number of results to return
     * @return List of recent email analyses
     */
    @Query(value = "SELECT e FROM EmailAnalysis e ORDER BY e.analyzedAt DESC LIMIT :limit")
    List<EmailAnalysis> findTopByOrderByAnalyzedAtDesc(@Param("limit") int limit);

    /**
     * Finds email analyses with the specified threat levels.
     *
     * @param threatLevels List of threat levels to search for
     * @return List of matching email analyses
     */
    List<EmailAnalysis> findByThreatLevelIn(List<EmailAnalysis.ThreatLevel> threatLevels);

    /**
     * Finds email analyses from a specific sender.
     *
     * @param sender Email sender to search for
     * @return List of matching email analyses
     */
    List<EmailAnalysis> findBySenderContaining(String sender);

    /**
     * Finds email analyses with a specific threat level and higher than a given score.
     *
     * @param threatLevel Threat level to search for
     * @param minScore    Minimum threat score
     * @return List of matching email analyses
     */
    List<EmailAnalysis> findByThreatLevelAndThreatScoreGreaterThan(
            EmailAnalysis.ThreatLevel threatLevel, double minScore);
}
