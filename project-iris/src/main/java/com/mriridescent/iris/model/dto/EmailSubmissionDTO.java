package com.mriridescent.iris.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for submitting an email for analysis.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailSubmissionDTO {

    private String emailId;
    private String sender;
    private String recipient;
    private String subject;
    private String content;
    private Map<String, String> metadata;
}
