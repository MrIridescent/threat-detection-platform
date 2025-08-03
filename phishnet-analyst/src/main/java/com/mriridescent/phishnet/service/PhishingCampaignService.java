package com.mriridescent.phishnet.service;

import com.mriridescent.phishnet.model.dto.PhishingCampaignDTO;
import com.mriridescent.phishnet.model.dto.CampaignCreationDTO;

import java.util.List;

/**
 * Service interface for phishing campaign operations.
 */
public interface PhishingCampaignService {

    /**
     * Creates a new phishing campaign.
     *
     * @param campaignDto The campaign creation data.
     * @return The created campaign.
     */
    PhishingCampaignDTO createCampaign(CampaignCreationDTO campaignDto);

    /**
     * Retrieves a campaign by ID.
     *
     * @param id The campaign ID.
     * @return The campaign.
     */
    PhishingCampaignDTO getCampaignById(Long id);

    /**
     * Retrieves all campaigns.
     *
     * @return A list of all campaigns.
     */
    List<PhishingCampaignDTO> getAllCampaigns();

    /**
     * Updates a campaign's status.
     *
     * @param id     The campaign ID.
     * @param status The new status.
     * @return The updated campaign.
     */
    PhishingCampaignDTO updateCampaignStatus(Long id, String status);

    /**
     * Retrieves campaigns by threat level.
     *
     * @param threatLevel The threat level to filter by.
     * @return A list of campaigns with the specified threat level.
     */
    List<PhishingCampaignDTO> getCampaignsByThreatLevel(String threatLevel);

    /**
     * Assigns a campaign to a user.
     *
     * @param id     The campaign ID.
     * @param userId The user ID.
     * @return The updated campaign.
     */
    PhishingCampaignDTO assignCampaign(Long id, Long userId);
}
