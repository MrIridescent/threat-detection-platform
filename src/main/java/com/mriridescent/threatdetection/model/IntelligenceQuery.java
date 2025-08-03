package com.mriridescent.threatdetection.model;

import lombok.Data;

/**
 * Represents a query for threat intelligence information.
 */
@Data
public class IntelligenceQuery {
    private String indicator;
    private IndicatorType indicatorType;
    private String context;

    public enum IndicatorType {
        IP,
        DOMAIN,
        URL,
        HASH,
        EMAIL,
        FILE_NAME
    }
}
