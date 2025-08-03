package com.mriridescent.threatdetection.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        userDetails = User.withUsername("testuser")
                .password("password")
                .authorities(new ArrayList<>())
                .build();
    }

    @Test
    void shouldGenerateAndValidateToken() {
        // Given
        Map<String, Object> extraClaims = new HashMap<>();

        // When
        String token = jwtService.generateToken(extraClaims, userDetails);

        // Then
        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void shouldExtractUsername() {
        // Given
        String token = jwtService.generateToken(userDetails);

        // When
        String username = jwtService.extractUsername(token);

        // Then
        assertEquals("testuser", username);
    }

    @Test
    void shouldReturnFalseForInvalidToken() {
        // Given
        String token = jwtService.generateToken(userDetails);
        UserDetails differentUser = User.withUsername("different")
                .password("password")
                .authorities(new ArrayList<>())
                .build();

        // When & Then
        assertFalse(jwtService.isTokenValid(token, differentUser));
    }
}
