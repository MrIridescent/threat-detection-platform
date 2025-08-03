package com.mriridescent.phishnet.repository;

import com.mriridescent.phishnet.model.entity.PhishingCampaign;
import com.mriridescent.phishnet.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for PhishingCampaign entities.
 */
@Repository
public interface PhishingCampaignRepository extends JpaRepository<PhishingCampaign, Long> {

    /**
     * Finds campaigns by status.
     *
     * @param status The status to filter by.
     * @return A list of campaigns with the specified status.
     */
    List<PhishingCampaign> findByStatus(PhishingCampaign.CampaignStatus status);

    /**
     * Finds campaigns by threat level.
     *
     * @param threatLevel The threat level to filter by.
     * @return A list of campaigns with the specified threat level.
     */
    List<PhishingCampaign> findByThreatLevel(PhishingCampaign.ThreatLevel threatLevel);

    /**
     * Finds campaigns by target sector.
     *
     * @param targetSector The target sector to filter by.
     * @return A list of campaigns targeting the specified sector.
     */
    List<PhishingCampaign> findByTargetSectorContainingIgnoreCase(String targetSector);

    /**
     * Finds campaigns by creator.
     *
     * @param createdBy The creator to filter by.
     * @return A list of campaigns created by the specified user.
     */
    List<PhishingCampaign> findByCreatedBy(User createdBy);

    /**
     * Finds campaigns by assignee.
     *
     * @param assignedTo The assignee to filter by.
     * @return A list of campaigns assigned to the specified user.
     */
    List<PhishingCampaign> findByAssignedTo(User assignedTo);

    /**
     * Finds campaigns by name.
     *
     * @param name The name to filter by.
     * @return A list of campaigns with the specified name.
     */
    List<PhishingCampaign> findByNameContainingIgnoreCase(String name);
}
