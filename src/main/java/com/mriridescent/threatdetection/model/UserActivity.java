package com.mriridescent.threatdetection.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Represents a user activity for behavioral analysis.
 */
@Data
public class UserActivity {
    private String activityId;
    private String userId;
    private String ipAddress;
    private String activityType;
    private String resourceAccessed;
    private boolean successful;
    private LocalDateTime timestamp;
    private String userAgent;
    private String location;
    private String sessionId;
}
