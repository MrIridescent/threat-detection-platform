package com.mriridescent.threatdetection.agent.detection;

import com.mriridescent.threatdetection.agent.core.AbstractAgent;
import com.mriridescent.threatdetection.agent.core.AgentTask;
import com.mriridescent.threatdetection.model.UserActivity;
import com.mriridescent.threatdetection.model.ThreatAlert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * AI agent responsible for analyzing user behavior patterns to detect anomalies.
 */
@Slf4j
@Component
public class BehaviorAnalysisAgent extends AbstractAgent {

    public BehaviorAnalysisAgent(@Qualifier("agentTaskExecutor") ThreadPoolTaskExecutor executor) {
        super("behavior-analysis", executor);
    }

    @Override
    public void initialize() {
        super.initialize();
        log.info("Behavior Analysis Agent initialized with user behavior models");
    }

    @Override
    protected <T, R> R processTask(AgentTask<T, R> task) {
        if (task.getInput() instanceof UserActivity activity) {
            log.debug("Processing user activity: {} for user {}", 
                     activity.getActivityType(), activity.getUserId());

            // Analyze user activity for anomalies
            double anomalyScore = calculateAnomalyScore(activity);

            if (anomalyScore > 0.85) {
                ThreatAlert alert = new ThreatAlert();
                alert.setUserId(activity.getUserId());
                alert.setSourceIp(activity.getIpAddress());
                alert.setDescription("Unusual user behavior detected: " + activity.getActivityType());
                alert.setSeverity(ThreatAlert.Severity.HIGH);
                alert.setDetectionTime(activity.getTimestamp());
                alert.setConfidenceScore(anomalyScore);

                return (R) alert;
            }

            return null;
        }

        throw new IllegalArgumentException("Unsupported task input type for BehaviorAnalysisAgent");
    }

    private double calculateAnomalyScore(UserActivity activity) {
        // Advanced AI behavior analysis would be implemented here
        // This is a simplified placeholder implementation

        // Check time of activity (is it outside normal hours?)
        int hour = activity.getTimestamp().getHour();
        double timeAnomalyFactor = (hour < 8 || hour > 18) ? 0.7 : 0.1;

        // Check if activity location is unusual
        double locationAnomalyFactor = isUnusualLocation(activity) ? 0.8 : 0.1;

        // Check if activity type is unusual for this user
        double activityAnomalyFactor = isUnusualActivityType(activity) ? 0.9 : 0.2;

        // Calculate weighted score
        return (0.3 * timeAnomalyFactor) + (0.3 * locationAnomalyFactor) + (0.4 * activityAnomalyFactor);
    }

    private boolean isUnusualLocation(UserActivity activity) {
        // In a real implementation, this would check against the user's normal locations
        return false;
    }

    private boolean isUnusualActivityType(UserActivity activity) {
        // In a real implementation, this would check against the user's normal activity patterns
        return activity.getActivityType().equals("DATABASE_DUMP") ||
               activity.getActivityType().equals("MASS_FILE_DOWNLOAD");
    }
}
