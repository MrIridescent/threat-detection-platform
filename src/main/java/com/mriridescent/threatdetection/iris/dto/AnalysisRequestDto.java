package com.mriridescent.threatdetection.iris.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Map;

/**
 * DTO for email analysis requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisRequestDto {

    @NotBlank(message = "Email subject is required")
    private String subject;

    @NotBlank(message = "Email sender is required")
    private String sender;

    @NotEmpty(message = "At least one recipient is required")
    private String[] recipients;

    @NotBlank(message = "Email body is required")
    private String body;

    private Map<String, String> headers;

    private String[] attachmentNames;

    private String clientIp;

    private String mailServer;

    private boolean prioritizeSpeed;
}
