package com.mriridescent.threatdetection.iris.repository;

import com.mriridescent.threatdetection.iris.model.entity.EmailFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for EmailFeature entities.
 */
@Repository
public interface EmailFeatureRepository extends JpaRepository<EmailFeature, Long> {

    /**
     * Finds features for a specific email analysis.
     *
     * @param emailAnalysisId Email analysis ID to search for
     * @return List of matching features
     */
    List<EmailFeature> findByEmailAnalysisId(Long emailAnalysisId);

    /**
     * Finds features by feature type.
     *
     * @param featureType Feature type to search for
     * @return List of matching features
     */
    List<EmailFeature> findByFeatureType(EmailFeature.FeatureType featureType);

    /**
     * Finds features with a specific name.
     *
     * @param featureName Feature name to search for
     * @return List of matching features
     */
    List<EmailFeature> findByFeatureName(String featureName);

    /**
     * Finds features with a weight above a threshold.
     *
     * @param minWeight Minimum weight to search for
     * @return List of matching features
     */
    List<EmailFeature> findByWeightGreaterThanEqual(double minWeight);
}
