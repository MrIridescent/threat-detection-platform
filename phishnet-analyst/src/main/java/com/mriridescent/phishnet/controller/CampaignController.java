package com.mriridescent.phishnet.controller;

import com.mriridescent.phishnet.model.dto.PhishingCampaignDTO;
import com.mriridescent.phishnet.model.dto.CampaignCreationDTO;
import com.mriridescent.phishnet.service.PhishingCampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for campaign operations.
 */
@RestController
@RequestMapping("/v1/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final PhishingCampaignService campaignService;

    /**
     * Creates a new phishing campaign.
     *
     * @param campaignDto The campaign creation data.
     * @return The created campaign.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<PhishingCampaignDTO> createCampaign(@RequestBody CampaignCreationDTO campaignDto) {
        return ResponseEntity.ok(campaignService.createCampaign(campaignDto));
    }

    /**
     * Retrieves a campaign by ID.
     *
     * @param id The campaign ID.
     * @return The campaign.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<PhishingCampaignDTO> getCampaign(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.getCampaignById(id));
    }

    /**
     * Retrieves all campaigns.
     *
     * @return A list of all campaigns.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<List<PhishingCampaignDTO>> getAllCampaigns() {
        return ResponseEntity.ok(campaignService.getAllCampaigns());
    }

    /**
     * Updates a campaign's status.
     *
     * @param id     The campaign ID.
     * @param status The new status.
     * @return The updated campaign.
     */
    @PutMapping("/{id}/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<PhishingCampaignDTO> updateCampaignStatus(
            @PathVariable Long id, @PathVariable String status) {
        return ResponseEntity.ok(campaignService.updateCampaignStatus(id, status));
    }

    /**
     * Retrieves campaigns by threat level.
     *
     * @param threatLevel The threat level to filter by.
     * @return A list of campaigns with the specified threat level.
     */
    @GetMapping("/threat-level/{threatLevel}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<List<PhishingCampaignDTO>> getCampaignsByThreatLevel(
            @PathVariable String threatLevel) {
        return ResponseEntity.ok(campaignService.getCampaignsByThreatLevel(threatLevel));
    }

    /**
     * Assigns a campaign to a user.
     *
     * @param id     The campaign ID.
     * @param userId The user ID.
     * @return The updated campaign.
     */
    @PutMapping("/{id}/assign/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PhishingCampaignDTO> assignCampaign(
            @PathVariable Long id, @PathVariable Long userId) {
        return ResponseEntity.ok(campaignService.assignCampaign(id, userId));
    }
}
