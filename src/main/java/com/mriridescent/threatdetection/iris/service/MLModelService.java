package com.mriridescent.threatdetection.iris.service;
package com.mriridescent.threatdetection.iris.service;

import com.mriridescent.threatdetection.iris.model.entity.MLModel;
import com.mriridescent.threatdetection.iris.repository.MLModelRepository;
import com.mriridescent.threatdetection.model.User;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing machine learning models.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MLModelService {

    private final MLModelRepository mlModelRepository;

    /**
     * Get all ML models
     *
     * @return List of all models
     */
    @Cacheable(value = "mlModels")
    @Timed(value = "iris.model.fetch.all", description = "Time taken to fetch all ML models")
    public List<MLModel> getAllModels() {
        log.info("Fetching all ML models");
        return mlModelRepository.findAll();
    }

    /**
     * Get all active ML models
     *
     * @return List of active models
     */
    @Cacheable(value = "activeModels")
    @Timed(value = "iris.model.fetch.active", description = "Time taken to fetch active ML models")
    public List<MLModel> getActiveModels() {
        log.info("Fetching active ML models");
        return mlModelRepository.findByActiveTrue();
    }

    /**
     * Get ML model by ID
     *
     * @param id Model ID
     * @return Optional containing the model if found
     */
    @Cacheable(value = "mlModel", key = "#id")
    public Optional<MLModel> getModelById(Long id) {
        log.info("Fetching ML model with ID: {}", id);
        return mlModelRepository.findById(id);
    }

    /**
     * Get ML model by name
     *
     * @param name Model name
     * @return Optional containing the model if found
     */
    @Cacheable(value = "mlModel", key = "#name")
    public Optional<MLModel> getModelByName(String name) {
        log.info("Fetching ML model with name: {}", name);
        return mlModelRepository.findByName(name);
    }

    /**
     * Create a new ML model
     *
     * @param model The model to create
     * @return The created model
     */
    @Transactional
    @CacheEvict(value = {"mlModels", "activeModels"}, allEntries = true)
    @Timed(value = "iris.model.create", description = "Time taken to create a new ML model")
    public MLModel createModel(MLModel model) {
        log.info("Creating new ML model: {}", model.getName());

        // Set current user as creator
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            model.setCreatedBy((User) authentication.getPrincipal());
        }

        model.setCreatedAt(LocalDateTime.now());
        return mlModelRepository.save(model);
    }

    /**
     * Update an existing ML model
     *
     * @param id Model ID
     * @param updatedModel Updated model data
     * @return The updated model
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "mlModel", key = "#id"),
        @CacheEvict(value = {"mlModels", "activeModels"}, allEntries = true)
    })
    @Timed(value = "iris.model.update", description = "Time taken to update an ML model")
    public MLModel updateModel(Long id, MLModel updatedModel) {
        log.info("Updating ML model with ID: {}", id);

        return mlModelRepository.findById(id)
                .map(existingModel -> {
                    existingModel.setName(updatedModel.getName());
                    existingModel.setVersion(updatedModel.getVersion());
                    existingModel.setDescription(updatedModel.getDescription());
                    existingModel.setFilePath(updatedModel.getFilePath());
                    existingModel.setAccuracy(updatedModel.getAccuracy());
                    existingModel.setPrecision(updatedModel.getPrecision());
                    existingModel.setRecall(updatedModel.getRecall());
                    existingModel.setF1Score(updatedModel.getF1Score());
                    existingModel.setActive(updatedModel.isActive());

                    if (updatedModel.getLastTrainedAt() != null) {
                        existingModel.setLastTrainedAt(updatedModel.getLastTrainedAt());
                    }

                    return mlModelRepository.save(existingModel);
                })
                .orElseThrow(() -> new RuntimeException("Model not found with id: " + id));
    }

    /**
     * Delete an ML model
     *
     * @param id Model ID
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "mlModel", key = "#id"),
        @CacheEvict(value = {"mlModels", "activeModels"}, allEntries = true)
    })
    public void deleteModel(Long id) {
        log.info("Deleting ML model with ID: {}", id);
        mlModelRepository.deleteById(id);
    }

    /**
     * Activate an ML model
     *
     * @param id Model ID
     * @return The activated model
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "mlModel", key = "#id"),
        @CacheEvict(value = {"mlModels", "activeModels"}, allEntries = true)
    })
    public MLModel activateModel(Long id) {
        log.info("Activating ML model with ID: {}", id);

        return mlModelRepository.findById(id)
                .map(model -> {
                    model.setActive(true);
                    return mlModelRepository.save(model);
                })
                .orElseThrow(() -> new RuntimeException("Model not found with id: " + id));
    }

    /**
     * Deactivate an ML model
     *
     * @param id Model ID
     * @return The deactivated model
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "mlModel", key = "#id"),
        @CacheEvict(value = {"mlModels", "activeModels"}, allEntries = true)
    })
    public MLModel deactivateModel(Long id) {
        log.info("Deactivating ML model with ID: {}", id);

        return mlModelRepository.findById(id)
                .map(model -> {
                    model.setActive(false);
                    return mlModelRepository.save(model);
                })
                .orElseThrow(() -> new RuntimeException("Model not found with id: " + id));
    }

    /**
     * Update model metrics after training
     *
     * @param id Model ID
     * @param accuracy New accuracy value
     * @param precision New precision value
     * @param recall New recall value
     * @param f1Score New F1 score value
     * @return The updated model
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "mlModel", key = "#id"),
        @CacheEvict(value = {"mlModels", "activeModels"}, allEntries = true)
    })
    public MLModel updateModelMetrics(Long id, double accuracy, double precision, double recall, double f1Score) {
        log.info("Updating metrics for ML model with ID: {}", id);

        return mlModelRepository.findById(id)
                .map(model -> {
                    model.setAccuracy(accuracy);
                    model.setPrecision(precision);
                    model.setRecall(recall);
                    model.setF1Score(f1Score);
                    model.setLastTrainedAt(LocalDateTime.now());
                    return mlModelRepository.save(model);
                })
                .orElseThrow(() -> new RuntimeException("Model not found with id: " + id));
    }
}
import com.mriridescent.threatdetection.iris.model.entity.EmailAnalysis;
import com.mriridescent.threatdetection.iris.model.entity.EmailFeature;
import com.mriridescent.threatdetection.iris.model.entity.MLModel;
import com.mriridescent.threatdetection.iris.repository.MLModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for managing and using ML models in Project Iris.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MLModelService {

    private final MLModelRepository modelRepository;
    private final Map<Long, MultiLayerNetwork> loadedModels = new HashMap<>();

    /**
     * Gets the active ML model for email analysis.
     *
     * @return The active model
     */
    public MLModel getActiveModel() {
        return modelRepository.findByActive(true)
                .orElseThrow(() -> new IllegalStateException("No active model found"));
    }

    /**
     * Predicts the threat score for an email based on its features.
     *
     * @param model    The ML model to use
     * @param features The extracted email features
     * @return The predicted threat score (0.0-1.0)
     */
    public double predictThreatScore(MLModel model, List<EmailFeature> features) {
        try {
            // Get or load the model
            MultiLayerNetwork network = getOrLoadModel(model);

            // Convert features to input vector
            INDArray input = featuresToInput(features);

            // Make prediction
            INDArray output = network.output(input, false);

            // Return the prediction score (assuming binary classification)
            return output.getDouble(0, 1);

        } catch (Exception e) {
            log.error("Error making prediction with model: {}", model.getName(), e);
            // Fallback to a simple rule-based score calculation
            return calculateFallbackScore(features);
        }
    }

    /**
     * Updates the model with feedback from a false positive.
     *
     * @param analysis The email analysis marked as false positive
     */
    public void updateModelWithFeedback(EmailAnalysis analysis) {
        // In a production system, this would collect the false positive
        // for later model retraining or online learning
        log.info("Recording false positive feedback for model training: {}", analysis.getId());

        // Simplified implementation - in production, would save to a dataset
        // for model retraining or implement online learning
    }

    /**
     * Gets or loads a model into memory.
     *
     * @param model The model metadata
     * @return The loaded neural network
     * @throws IOException If the model cannot be loaded
     */
    private MultiLayerNetwork getOrLoadModel(MLModel model) throws IOException {
        if (loadedModels.containsKey(model.getId())) {
            return loadedModels.get(model.getId());
        }

        // Load model from file
        File modelFile = new File(model.getFilePath());
        MultiLayerNetwork network = ModelSerializer.restoreMultiLayerNetwork(modelFile);
        loadedModels.put(model.getId(), network);

        return network;
    }

    /**
     * Converts email features to an input vector for the ML model.
     *
     * @param features The email features
     * @return Input vector for the neural network
     */
    private INDArray featuresToInput(List<EmailFeature> features) {
        // In a real implementation, this would convert features to the exact
        // format expected by the trained model

        // Simplified implementation for demonstration
        int featureCount = 50; // Assuming model expects 50 features
        INDArray input = Nd4j.zeros(1, featureCount);

        // Feature mapping would be defined based on the trained model
        Map<String, Integer> featureIndices = getFeatureIndices();

        // Set values in the input vector
        for (EmailFeature feature : features) {
            if (featureIndices.containsKey(feature.getFeatureName())) {
                int index = featureIndices.get(feature.getFeatureName());

                // Convert feature value to numeric
                double value;
                if (feature.getFeatureValue().equalsIgnoreCase("true") || 
                    feature.getFeatureValue().equalsIgnoreCase("false")) {
                    value = Boolean.parseBoolean(feature.getFeatureValue()) ? 1.0 : 0.0;
                } else {
                    try {
                        value = Double.parseDouble(feature.getFeatureValue());
                    } catch (NumberFormatException e) {
                        // Use feature weight as a fallback
                        value = feature.getWeight();
                    }
                }

                input.putScalar(0, index, value);
            }
        }

        return input;
    }

    /**
     * Returns a mapping of feature names to indices in the input vector.
     *
     * @return Map of feature names to indices
     */
    private Map<String, Integer> getFeatureIndices() {
        // This would be defined based on the trained model
        // Simplified example:
        Map<String, Integer> indices = new HashMap<>();
        indices.put("sender_domain", 0);
        indices.put("display_name_mismatch", 1);
        indices.put("subject_urgency", 2);
        indices.put("subject_excessive_punctuation", 3);
        indices.put("short_subject", 4);
        indices.put("suspicious_keyword_density", 5);
        indices.put("asks_for_credentials", 6);
        indices.put("contains_form", 7);
        indices.put("obfuscated_links", 8);
        indices.put("urgent_tone", 9);
        indices.put("spf_failed", 10);
        indices.put("dkim_failed", 11);
        indices.put("unusual_routing", 12);
        indices.put("new_domain", 13);
        indices.put("ip_based_urls", 14);
        indices.put("url_shorteners", 15);
        indices.put("mismatched_links", 16);
        indices.put("fear_tactics", 17);
        indices.put("reward_tactics", 18);
        indices.put("authority_impersonation", 19);
        indices.put("poor_language_quality", 20);
        // Additional features would be mapped to indices 21-49

        return indices;
    }

    /**
     * Calculates a fallback threat score based on feature weights.
     *
     * @param features The email features
     * @return A calculated threat score
     */
    private double calculateFallbackScore(List<EmailFeature> features) {
        double totalWeight = 0.0;
        double weightedSum = 0.0;

        for (EmailFeature feature : features) {
            double value;
            if (feature.getFeatureValue().equalsIgnoreCase("true")) {
                value = 1.0;
            } else if (feature.getFeatureValue().equalsIgnoreCase("false")) {
                value = 0.0;
            } else {
                try {
                    value = Double.parseDouble(feature.getFeatureValue());
                    // Normalize if needed
                    if (value > 1.0) value = 1.0;
                } catch (NumberFormatException e) {
                    // Skip features with non-numeric values
                    continue;
                }
            }

            double weight = Math.abs(feature.getWeight());
            weightedSum += value * weight;
            totalWeight += weight;
        }

        if (totalWeight == 0.0) return 0.5; // Default mid-point if no weights

        return weightedSum / totalWeight;
    }
}
