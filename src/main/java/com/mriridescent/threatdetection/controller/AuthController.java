package com.mriridescent.threatdetection.controller;

import com.mriridescent.threatdetection.dto.AuthRequest;
import com.mriridescent.threatdetection.dto.AuthResponse;
import com.mriridescent.threatdetection.dto.RegisterRequest;
import com.mriridescent.threatdetection.model.User;
import com.mriridescent.threatdetection.security.JwtService;
import com.mriridescent.threatdetection.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for authentication operations.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    /**
     * Authenticate a user and generate JWT tokens
     *
     * @param request The authentication request
     * @return The authentication response with tokens
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return ResponseEntity.ok(AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build());
    }

    /**
     * Register a new user
     *
     * @param request The registration request
     * @return The registration response
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.createUser(request);
            String jwtToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            return ResponseEntity.ok(AuthResponse.builder()
                    .token(jwtToken)
                    .refreshToken(refreshToken)
                    .username(user.getUsername())
                    .role(user.getRole().name())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
