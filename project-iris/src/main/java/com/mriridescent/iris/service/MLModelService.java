package com.mriridescent.iris.service;

import com.mriridescent.iris.model.entity.EmailAnalysis;
import com.mriridescent.iris.model.entity.MLModel;

import java.util.Map;

/**
 * Service interface for machine learning model operations.
 */
public interface MLModelService {

    /**
     * Predicts the threat level and score for an email.
     *
     * @param emailContent The email content to analyze.
     * @param metadata     The email metadata.
     * @return A map containing the threat level and score.
     */
    Map<String, Object> predictThreat(String emailContent, Map<String, String> metadata);

    /**
     * Trains a new model with the latest data.
     *
     * @param modelType The type of model to train.
     * @return The trained model.
     */
    MLModel trainNewModel(String modelType);

    /**
     * Gets the active model of a specific type.
     *
     * @param modelType The type of model to retrieve.
     * @return The active model of the specified type.
     */
    MLModel getActiveModel(String modelType);

    /**
     * Updates the model based on feedback (e.g., false positive).
     *
     * @param analysis The email analysis with feedback.
     */
    void updateModelWithFeedback(EmailAnalysis analysis);
}
