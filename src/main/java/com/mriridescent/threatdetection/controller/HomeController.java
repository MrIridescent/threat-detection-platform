package com.mriridescent.threatdetection.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for home and basic informational endpoints.
 */
@RestController
@RequestMapping("/api/v1/public")
public class HomeController {

    /**
     * Welcome endpoint that provides basic information about the API
     *
     * @return Welcome message and API information
     */
    @GetMapping("/welcome")
    public ResponseEntity<ApiResponse<Map<String, Object>>> welcome() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Threat Detection Platform API");
        info.put("version", "1.0.0");
        info.put("description", "API for the Threat Detection Platform, integrating Project Iris and PhishNet Analyst");
        info.put("documentation", "/swagger-ui.html");

        return ResponseEntity.ok(ApiResponse.success("Welcome to the Threat Detection Platform API", info));
    }

    /**
     * Health check endpoint
     *
     * @return Health status
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("API is healthy"));
    }
}
