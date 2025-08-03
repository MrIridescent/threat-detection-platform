package com.mriridescent.iris.repository;

import com.mriridescent.iris.model.entity.EmailAnalysis;
import com.mriridescent.iris.model.entity.EmailFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for EmailFeature entities.
 */
@Repository
public interface EmailFeatureRepository extends JpaRepository<EmailFeature, Long> {

    /**
     * Finds features by email analysis.
     *
     * @param emailAnalysis The email analysis to filter by.
     * @return A list of features for the specified email analysis.
     */
    List<EmailFeature> findByEmailAnalysis(EmailAnalysis emailAnalysis);

    /**
     * Finds features by feature type.
     *
     * @param featureType The feature type to filter by.
     * @return A list of features with the specified type.
     */
    List<EmailFeature> findByFeatureType(EmailFeature.FeatureType featureType);

    /**
     * Finds features by feature name.
     *
     * @param featureName The feature name to filter by.
     * @return A list of features with the specified name.
     */
    List<EmailFeature> findByFeatureName(String featureName);

    /**
     * Finds features by email analysis and feature type.
     *
     * @param emailAnalysis The email analysis to filter by.
     * @param featureType   The feature type to filter by.
     * @return A list of features for the specified email analysis and type.
     */
    List<EmailFeature> findByEmailAnalysisAndFeatureType(EmailAnalysis emailAnalysis, EmailFeature.FeatureType featureType);

    /**
     * Finds features with weight above a threshold.
     *
     * @param weight The weight threshold.
     * @return A list of features with weight above the specified threshold.
     */
    List<EmailFeature> findByWeightGreaterThan(double weight);
}
