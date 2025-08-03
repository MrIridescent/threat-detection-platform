package com.mriridescent.threatdetection.phishnet.repository;

import com.mriridescent.threatdetection.phishnet.model.entity.PhishingCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PhishingCampaign entities.
 */
@Repository
public interface PhishingCampaignRepository extends JpaRepository<PhishingCampaign, Long> {
package com.mriridescent.threatdetection.phishnet.repository;

import com.mriridescent.threatdetection.phishnet.model.entity.PhishingCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for phishing campaign entity operations.
 */
@Repository
public interface PhishingCampaignRepository extends JpaRepository<PhishingCampaign, Long> {

    /**
     * Find campaigns by status
     *
     * @param status Campaign status
     * @return List of campaigns with the specified status
     */
    List<PhishingCampaign> findByStatus(PhishingCampaign.CampaignStatus status);

    /**
     * Find campaigns by severity
     *
     * @param severity Campaign severity
     * @return List of campaigns with the specified severity
     */
    List<PhishingCampaign> findBySeverity(PhishingCampaign.Severity severity);

    /**
     * Find campaigns with any of the specified statuses
     *
     * @param statuses List of campaign statuses
     * @return List of campaigns with any of the specified statuses
     */
    List<PhishingCampaign> findByStatusIn(List<PhishingCampaign.CampaignStatus> statuses);

    /**
     * Find campaigns created after a specific date
     *
     * @param date Date threshold
     * @return List of campaigns created after the specified date
     */
    List<PhishingCampaign> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find campaigns observed after a specific date
     *
     * @param date Date threshold
     * @return List of campaigns observed after the specified date
     */
    List<PhishingCampaign> findByLastObservedAtAfter(LocalDateTime date);

    /**
     * Find campaigns by name or description containing the search term (case insensitive)
     *
     * @param name Search term for name
     * @param description Search term for description
     * @return List of matching campaigns
     */
    List<PhishingCampaign> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);

    /**
     * Find campaigns targeting a specific demographic
     *
     * @param demographic Target demographic
     * @return List of campaigns targeting the specified demographic
     */
    List<PhishingCampaign> findByTargetDemographicContainingIgnoreCase(String demographic);

    /**
     * Find campaigns originating from a specific region
     *
     * @param region Origin region
     * @return List of campaigns from the specified region
     */
    List<PhishingCampaign> findByOriginRegionContainingIgnoreCase(String region);
}
    /**
     * Finds campaigns with the specified statuses.
     *
     * @param statuses List of statuses to search for
     * @return List of matching campaigns
     */
    List<PhishingCampaign> findByStatusIn(List<PhishingCampaign.CampaignStatus> statuses);

    /**
     * Finds campaigns by target sector.
     *
     * @param sector Target sector to search for
     * @return List of matching campaigns
     */
    List<PhishingCampaign> findByTargetSectorContainingIgnoreCase(String sector);

    /**
     * Finds campaigns with the specified threat level.
     *
     * @param threatLevel Threat level to search for
     * @return List of matching campaigns
     */
    List<PhishingCampaign> findByThreatLevel(PhishingCampaign.ThreatLevel threatLevel);

    /**
     * Finds a campaign by ID with eager loading of nodes and vectors.
     *
     * @param id Campaign ID to search for
     * @return Optional containing the campaign if found
     */
    @Query("SELECT c FROM PhishingCampaign c " +
           "LEFT JOIN FETCH c.infrastructureNodes " +
           "LEFT JOIN FETCH c.attackVectors " +
           "WHERE c.id = :id")
    Optional<PhishingCampaign> findByIdWithNodesAndVectors(@Param("id") Long id);
}
