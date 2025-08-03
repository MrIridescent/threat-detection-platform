package com.mriridescent.threatdetection.agent.response;

import com.mriridescent.threatdetection.agent.core.AbstractAgent;
import com.mriridescent.threatdetection.agent.core.AgentTask;
import com.mriridescent.threatdetection.model.ResponseAction;
import com.mriridescent.threatdetection.model.ThreatAlert;
import com.mriridescent.threatdetection.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * AI agent responsible for determining appropriate responses to detected threats.
 */
@Slf4j
@Component
public class ThreatResponseAgent extends AbstractAgent {

    private final NotificationService notificationService;

    public ThreatResponseAgent(
            @Qualifier("agentTaskExecutor") ThreadPoolTaskExecutor executor,
            NotificationService notificationService) {
        super("threat-response", executor);
        this.notificationService = notificationService;
    }

    @Override
    public void initialize() {
        super.initialize();
        log.info("Threat Response Agent initialized with response decision models");
    }

    @Override
    protected <T, R> R processTask(AgentTask<T, R> task) {
        if (task.getInput() instanceof ThreatAlert alert) {
            log.debug("Processing threat alert: {} with severity {}", 
                     alert.getAlertId(), alert.getSeverity());

            // Determine appropriate response actions
            List<ResponseAction> actions = determineResponseActions(alert);

            // Execute high-priority automatic actions
            executeAutomaticActions(actions, alert);

            return (R) actions;
        }

        throw new IllegalArgumentException("Unsupported task input type for ThreatResponseAgent");
    }

    private List<ResponseAction> determineResponseActions(ThreatAlert alert) {
        List<ResponseAction> actions = new ArrayList<>();

        // Decision logic based on threat type and severity
        switch (alert.getSeverity()) {
            case CRITICAL:
                actions.add(new ResponseAction("BLOCK_IP", alert.getSourceIp(), true));
                actions.add(new ResponseAction("NOTIFY_SECURITY_TEAM", alert.getDescription(), true));
                actions.add(new ResponseAction("ISOLATE_SYSTEM", alert.getSystemId(), false));
                break;

            case HIGH:
                actions.add(new ResponseAction("RATE_LIMIT_IP", alert.getSourceIp(), true));
                actions.add(new ResponseAction("NOTIFY_SECURITY_TEAM", alert.getDescription(), true));
                break;

            case MEDIUM:
                actions.add(new ResponseAction("INCREASE_MONITORING", alert.getSourceIp(), true));
                actions.add(new ResponseAction("NOTIFY_ADMIN", alert.getDescription(), true));
                break;

            case LOW:
                actions.add(new ResponseAction("LOG_ALERT", alert.getDescription(), true));
                break;
        }

        // Additional logic based on specific threat types could be added here

        return actions;
    }

    private void executeAutomaticActions(List<ResponseAction> actions, ThreatAlert alert) {
        for (ResponseAction action : actions) {
            if (action.isAutomatic()) {
                log.info("Executing automatic action: {} for alert {}", 
                         action.getActionType(), alert.getAlertId());

                switch (action.getActionType()) {
                    case "NOTIFY_SECURITY_TEAM":
                    case "NOTIFY_ADMIN":
                        notificationService.sendNotification(action.getTarget(), alert);
                        break;

                    case "BLOCK_IP":
                        // Implementation would connect to network security systems
                        log.info("Blocking IP address: {}", action.getTarget());
                        break;

                    case "RATE_LIMIT_IP":
                        // Implementation would apply rate limiting
                        log.info("Rate limiting IP address: {}", action.getTarget());
                        break;

                    case "INCREASE_MONITORING":
                        // Implementation would increase monitoring sensitivity
                        log.info("Increasing monitoring for: {}", action.getTarget());
                        break;

                    case "LOG_ALERT":
                        // Already logged by the system
                        break;
                }
            }
        }
    }
}
