package com.mriridescent.threatdetection.phishnet.repository;
package com.mriridescent.threatdetection.phishnet.repository;

import com.mriridescent.threatdetection.phishnet.model.entity.AttackVector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for attack vector entity operations.
 */
@Repository
public interface AttackVectorRepository extends JpaRepository<AttackVector, Long> {

    /**
     * Find attack vectors by campaign ID
     *
     * @param campaignId Campaign ID
     * @return List of attack vectors for the campaign
     */
    List<AttackVector> findByCampaignId(Long campaignId);

    /**
     * Find attack vectors by vector type
     *
     * @param vectorType Vector type
     * @return List of attack vectors of the specified type
     */
    List<AttackVector> findByVectorType(AttackVector.VectorType vectorType);

    /**
     * Find attack vectors by status
     *
     * @param status Vector status
     * @return List of attack vectors with the specified status
     */
    List<AttackVector> findByStatus(AttackVector.VectorStatus status);

    /**
     * Find attack vectors identified after a specific date
     *
     * @param date Date threshold
     * @return List of attack vectors identified after the specified date
     */
    List<AttackVector> findByIdentifiedAtAfter(LocalDateTime date);

    /**
     * Find attack vectors observed after a specific date
     *
     * @param date Date threshold
     * @return List of attack vectors observed after the specified date
     */
    List<AttackVector> findByLastObservedAtAfter(LocalDateTime date);

    /**
     * Find attack vectors by prevalence range
     *
     * @param minPrevalence Minimum prevalence
     * @param maxPrevalence Maximum prevalence
     * @return List of attack vectors within the specified prevalence range
     */
    List<AttackVector> findByPrevalenceBetween(int minPrevalence, int maxPrevalence);

    /**
     * Find attack vectors by effectiveness range
     *
     * @param minEffectiveness Minimum effectiveness
     * @param maxEffectiveness Maximum effectiveness
     * @return List of attack vectors within the specified effectiveness range
     */
    List<AttackVector> findByEffectivenessBetween(int minEffectiveness, int maxEffectiveness);

    /**
     * Find attack vectors by campaign ID and vector type
     *
     * @param campaignId Campaign ID
     * @param vectorType Vector type
     * @return List of attack vectors for the campaign of the specified type
     */
    List<AttackVector> findByCampaignIdAndVectorType(Long campaignId, AttackVector.VectorType vectorType);
}
import com.mriridescent.threatdetection.phishnet.model.entity.AttackVector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for AttackVector entities.
 */
@Repository
public interface AttackVectorRepository extends JpaRepository<AttackVector, Long> {

    /**
     * Finds vectors by vector type.
     *
     * @param vectorType Vector type to search for
     * @return List of matching vectors
     */
    List<AttackVector> findByVectorType(AttackVector.VectorType vectorType);

    /**
     * Finds vectors for a specific campaign.
     *
     * @param campaignId Campaign ID to search for
     * @return List of matching vectors
     */
    List<AttackVector> findByCampaignId(Long campaignId);

    /**
     * Finds vectors with a specific status.
     *
     * @param status Status to search for
     * @return List of matching vectors
     */
    List<AttackVector> findByStatus(AttackVector.VectorStatus status);

    /**
     * Finds vectors with a prevalence greater than the specified value.
     *
     * @param minPrevalence Minimum prevalence value
     * @return List of matching vectors
     */
    List<AttackVector> findByPrevalenceGreaterThanEqual(int minPrevalence);

    /**
     * Finds vectors with an effectiveness greater than the specified value.
     *
     * @param minEffectiveness Minimum effectiveness value
     * @return List of matching vectors
     */
    List<AttackVector> findByEffectivenessGreaterThanEqual(int minEffectiveness);
}
