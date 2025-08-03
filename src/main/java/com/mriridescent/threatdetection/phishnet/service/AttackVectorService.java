package com.mriridescent.threatdetection.phishnet.service;

import com.mriridescent.threatdetection.phishnet.model.entity.AttackVector;
import com.mriridescent.threatdetection.phishnet.model.entity.PhishingCampaign;
import com.mriridescent.threatdetection.phishnet.repository.AttackVectorRepository;
import com.mriridescent.threatdetection.phishnet.repository.PhishingCampaignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing attack vectors.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AttackVectorService {

    private final AttackVectorRepository attackVectorRepository;
    private final PhishingCampaignRepository campaignRepository;

    /**
     * Get all attack vectors
     *
     * @return List of all attack vectors
     */
    @Cacheable(value = "attackVectors")
    public List<AttackVector> getAllAttackVectors() {
        log.info("Fetching all attack vectors");
        return attackVectorRepository.findAll();
    }

    /**
     * Get attack vectors for a campaign
     *
     * @param campaignId Campaign ID
     * @return List of attack vectors for the campaign
     */
    @Cacheable(value = "campaignAttackVectors", key = "#campaignId")
    public List<AttackVector> getAttackVectorsByCampaign(Long campaignId) {
        log.info("Fetching attack vectors for campaign with ID: {}", campaignId);
        return attackVectorRepository.findByCampaignId(campaignId);
    }

    /**
     * Get attack vector by ID
     *
     * @param id Attack vector ID
     * @return Optional containing the attack vector if found
     */
    @Cacheable(value = "attackVector", key = "#id")
    public Optional<AttackVector> getAttackVectorById(Long id) {
        log.info("Fetching attack vector with ID: {}", id);
        return attackVectorRepository.findById(id);
    }

    /**
     * Create a new attack vector for a campaign
     *
     * @param campaignId Campaign ID
     * @param attackVector The attack vector to create
     * @return The created attack vector
     */
    @Transactional
    @CacheEvict(value = {"attackVectors", "campaignAttackVectors", "phishingCampaign"}, 
                allEntries = true, key = "#campaignId")
    public AttackVector createAttackVector(Long campaignId, AttackVector attackVector) {
        log.info("Creating new attack vector for campaign with ID: {}", campaignId);

        return campaignRepository.findById(campaignId)
                .map(campaign -> {
                    attackVector.setCampaign(campaign);
                    attackVector.setIdentifiedAt(LocalDateTime.now());
                    attackVector.setLastObservedAt(LocalDateTime.now());

                    AttackVector savedVector = attackVectorRepository.save(attackVector);

                    // Update the campaign's last updated timestamp
                    campaign.setLastUpdatedAt(LocalDateTime.now());
                    campaignRepository.save(campaign);

                    return savedVector;
                })
                .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + campaignId));
    }

    /**
     * Update an existing attack vector
     *
     * @param id Attack vector ID
     * @param updatedVector Updated attack vector data
     * @return The updated attack vector
     */
    @Transactional
    @CacheEvict(value = {"attackVector", "attackVectors", "campaignAttackVectors", "phishingCampaign"}, 
                allEntries = true)
    public AttackVector updateAttackVector(Long id, AttackVector updatedVector) {
        log.info("Updating attack vector with ID: {}", id);

        return attackVectorRepository.findById(id)
                .map(existingVector -> {
                    existingVector.setName(updatedVector.getName());
                    existingVector.setVectorType(updatedVector.getVectorType());
                    existingVector.setDescription(updatedVector.getDescription());
                    existingVector.setStatus(updatedVector.getStatus());
                    existingVector.setPrevalence(updatedVector.getPrevalence());
                    existingVector.setEffectiveness(updatedVector.getEffectiveness());
                    existingVector.setTechnicalDetails(updatedVector.getTechnicalDetails());
                    existingVector.setMitigationSteps(updatedVector.getMitigationSteps());
                    existingVector.setLastObservedAt(LocalDateTime.now());

                    AttackVector savedVector = attackVectorRepository.save(existingVector);

                    // Update the campaign's last updated timestamp
                    PhishingCampaign campaign = existingVector.getCampaign();
                    if (campaign != null) {
                        campaign.setLastUpdatedAt(LocalDateTime.now());
                        campaignRepository.save(campaign);
                    }

                    return savedVector;
                })
                .orElseThrow(() -> new RuntimeException("Attack vector not found with id: " + id));
    }

    /**
     * Delete an attack vector
     *
     * @param id Attack vector ID
     */
    @Transactional
    @CacheEvict(value = {"attackVector", "attackVectors", "campaignAttackVectors", "phishingCampaign"}, 
                allEntries = true)
    public void deleteAttackVector(Long id) {
        log.info("Deleting attack vector with ID: {}", id);

        attackVectorRepository.findById(id).ifPresent(vector -> {
            // Update the campaign's last updated timestamp before deleting the vector
            PhishingCampaign campaign = vector.getCampaign();
            if (campaign != null) {
                campaign.setLastUpdatedAt(LocalDateTime.now());
                campaignRepository.save(campaign);
            }

            attackVectorRepository.deleteById(id);
        });
    }

    /**
     * Update attack vector status
     *
     * @param id Attack vector ID
     * @param status New status
     * @return The updated attack vector
     */
    @Transactional
    @CacheEvict(value = {"attackVector", "attackVectors", "campaignAttackVectors", "phishingCampaign"}, 
                allEntries = true)
    public AttackVector updateAttackVectorStatus(Long id, AttackVector.VectorStatus status) {
        log.info("Updating status of attack vector with ID: {} to {}", id, status);

        return attackVectorRepository.findById(id)
                .map(vector -> {
                    vector.setStatus(status);
                    vector.setLastObservedAt(LocalDateTime.now());

                    AttackVector savedVector = attackVectorRepository.save(vector);

                    // Update the campaign's last updated timestamp
                    PhishingCampaign campaign = vector.getCampaign();
                    if (campaign != null) {
                        campaign.setLastUpdatedAt(LocalDateTime.now());
                        campaignRepository.save(campaign);
                    }

                    return savedVector;
                })
                .orElseThrow(() -> new RuntimeException("Attack vector not found with id: " + id));
    }

    /**
     * Get attack vectors by type
     *
     * @param vectorType Vector type
     * @return List of attack vectors of the specified type
     */
    public List<AttackVector> getAttackVectorsByType(AttackVector.VectorType vectorType) {
        log.info("Fetching attack vectors of type: {}", vectorType);
        return attackVectorRepository.findByVectorType(vectorType);
    }

    /**
     * Get active attack vectors
     *
     * @return List of active attack vectors
     */
    public List<AttackVector> getActiveAttackVectors() {
        log.info("Fetching active attack vectors");
        return attackVectorRepository.findByStatus(AttackVector.VectorStatus.ACTIVE);
    }
}
