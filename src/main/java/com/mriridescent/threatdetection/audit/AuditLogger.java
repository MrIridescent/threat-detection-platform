package com.mriridescent.threatdetection.audit;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Enterprise-grade audit logging component for compliance and security monitoring.
 * Logs security events and user actions in a structured format suitable for SIEM integration.
 */
@Component
@RequiredArgsConstructor
public class AuditLogger {

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT_LOG");

    /**
     * Log a security event with user information
     *
     * @param eventType The type of security event
     * @param resourceType The type of resource being accessed
     * @param resourceId The identifier of the resource
     * @param action The action being performed
     * @param outcome The outcome of the action
     * @param details Additional details
     */
    public void logSecurityEvent(
            EventType eventType,
            String resourceType,
            String resourceId,
            String action,
            String outcome,
            String details) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "anonymous";

        StringBuilder logEntry = new StringBuilder();
        logEntry.append("SECURITY_EVENT [")
                .append(eventType).append("] ")
                .append("user='")
                .append(username)
                .append("' ")
                .append("resource='")
                .append(resourceType)
                .append("/")
                .append(resourceId)
                .append("' ")
                .append("action='")
                .append(action)
                .append("' ")
                .append("outcome='")
                .append(outcome)
                .append("' ")
                .append("details='")
                .append(details)
                .append("'");

        auditLogger.info(logEntry.toString());
    }

    /**
     * Log a data access event
     *
     * @param resourceType The type of resource being accessed
     * @param resourceId The identifier of the resource
     * @param action The action being performed (READ, WRITE, DELETE)
     */
    public void logDataAccess(String resourceType, String resourceId, String action) {
        logSecurityEvent(EventType.DATA_ACCESS, resourceType, resourceId, action, "SUCCESS", "");
    }

    /**
     * Log an authentication event
     *
     * @param username The username attempting authentication
     * @param outcome The outcome of the authentication attempt
     * @param details Additional details about the authentication
     */
    public void logAuthentication(String username, String outcome, String details) {
        logSecurityEvent(EventType.AUTHENTICATION, "USER", username, "LOGIN", outcome, details);
    }

    /**
     * Log a threat detection event
     * 
     * @param threatType The type of threat detected
     * @param threatId The identifier of the threat
     * @param severity The severity of the threat
     * @param details Additional details about the threat
     */
    public void logThreatDetection(String threatType, String threatId, String severity, String details) {
        logSecurityEvent(EventType.THREAT_DETECTION, threatType, threatId, "DETECT", severity, details);
    }

    public enum EventType {
        AUTHENTICATION,
        AUTHORIZATION,
        DATA_ACCESS,
        CONFIGURATION_CHANGE,
        THREAT_DETECTION
    }
}
