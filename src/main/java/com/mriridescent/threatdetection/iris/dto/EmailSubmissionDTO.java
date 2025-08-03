package com.mriridescent.threatdetection.iris.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO for submitting an email for analysis.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailSubmissionDTO {

    @NotBlank(message = "Email ID is required")
    private String emailId;

    @NotBlank(message = "Sender is required")
    @Email(message = "Sender must be a valid email address")
    private String sender;

    @NotBlank(message = "Recipient is required")
    @Email(message = "Recipient must be a valid email address")
    private String recipient;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Metadata cannot be null")
    private Map<String, String> metadata = new HashMap<>();
}
