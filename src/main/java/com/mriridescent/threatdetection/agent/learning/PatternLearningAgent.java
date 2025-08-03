package com.mriridescent.threatdetection.agent.learning;

import com.mriridescent.threatdetection.agent.core.AbstractAgent;
import com.mriridescent.threatdetection.agent.core.AgentTask;
import com.mriridescent.threatdetection.model.LearningData;
import com.mriridescent.threatdetection.model.ModelUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * AI agent responsible for continuously learning and updating threat detection models.
 */
@Slf4j
@Component
public class PatternLearningAgent extends AbstractAgent {

    private final List<LearningData> trainingDataBuffer = new ArrayList<>();

    public PatternLearningAgent(@Qualifier("agentTaskExecutor") ThreadPoolTaskExecutor executor) {
        super("pattern-learning", executor);
    }

    @Override
    public void initialize() {
        super.initialize();
        log.info("Pattern Learning Agent initialized with ML training capabilities");
    }

    @Override
    protected <T, R> R processTask(AgentTask<T, R> task) {
        if (task.getInput() instanceof LearningData data) {
            log.debug("Processing learning data for model: {}", data.getModelType());

            // Add to training data buffer
            synchronized (trainingDataBuffer) {
                trainingDataBuffer.add(data);
            }

            // Check if we should trigger immediate model update
            if (data.isPriority() || trainingDataBuffer.size() >= 100) {
                return (R) updateModel(data.getModelType());
            }

            return null;
        }

        throw new IllegalArgumentException("Unsupported task input type for PatternLearningAgent");
    }

    /**
     * Scheduled method to periodically update models with collected training data
     */
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void scheduledModelUpdate() {
        log.info("Running scheduled model update");

        synchronized (trainingDataBuffer) {
            if (trainingDataBuffer.isEmpty()) {
                log.info("No training data available for model update");
                return;
            }

            // Group by model type
            trainingDataBuffer.stream()
                .map(LearningData::getModelType)
                .distinct()
                .forEach(this::updateModel);

            trainingDataBuffer.clear();
        }
    }

    private ModelUpdate updateModel(String modelType) {
        log.info("Updating model: {}", modelType);

        // Extract relevant training data
        List<LearningData> relevantData;
        synchronized (trainingDataBuffer) {
            relevantData = trainingDataBuffer.stream()
                .filter(data -> data.getModelType().equals(modelType))
                .toList();
        }

        if (relevantData.isEmpty()) {
            log.warn("No training data available for model: {}", modelType);
            return null;
        }

        // In a real implementation, this would train/update ML models
        // This is a simplified placeholder implementation
        ModelUpdate update = new ModelUpdate();
        update.setModelType(modelType);
        update.setUpdateTime(java.time.LocalDateTime.now());
        update.setTrainingDataSize(relevantData.size());
        update.setPerformanceMetrics(Map.of(
            "accuracy", 0.95,
            "precision", 0.92,
            "recall", 0.89,
            "f1", 0.91
        ));

        log.info("Model update completed for: {} with {} training samples", 
                modelType, relevantData.size());

        return update;
    }
}
