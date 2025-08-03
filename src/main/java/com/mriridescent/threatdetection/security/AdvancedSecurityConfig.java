package com.mriridescent.threatdetection.security;

import com.mriridescent.threatdetection.security.filter.RateLimitingFilter;
import com.mriridescent.threatdetection.security.filter.ThreatDetectionFilter;
import com.mriridescent.threatdetection.security.filter.AuditLoggingFilter;
import com.mriridescent.threatdetection.security.handler.CustomAccessDeniedHandler;
import com.mriridescent.threatdetection.security.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Advanced enterprise security configuration with comprehensive protection layers.
 * Implements defense-in-depth strategy with multiple security controls.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
@Profile("!test")
public class AdvancedSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final RateLimitingFilter rateLimitingFilter;
    private final ThreatDetectionFilter threatDetectionFilter;
    private final AuditLoggingFilter auditLoggingFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // CSRF Protection with custom token repository
                .csrf(csrf -> csrf
                        .csrfTokenRepository(customCsrfTokenRepository())
                        .ignoringRequestMatchers("/api/v1/auth/**", "/api/v1/public/**")
                )
                
                // CORS Configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // Content Security Policy
                .headers(headers -> headers
                        .frameOptions().deny()
                        .contentTypeOptions().and()
                        .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                                .maxAgeInSeconds(31536000)
                                .includeSubdomains(true)
                                .preload(true)
                        )
                        .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                        .and()
                        .addHeaderWriter((request, response) -> {
                            response.setHeader("Content-Security-Policy", 
                                "default-src 'self'; " +
                                "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                                "style-src 'self' 'unsafe-inline'; " +
                                "img-src 'self' data: https:; " +
                                "font-src 'self'; " +
                                "connect-src 'self'; " +
                                "frame-ancestors 'none';"
                            );
                            response.setHeader("X-Content-Type-Options", "nosniff");
                            response.setHeader("X-Frame-Options", "DENY");
                            response.setHeader("X-XSS-Protection", "1; mode=block");
                            response.setHeader("Permissions-Policy", 
                                "geolocation=(), microphone=(), camera=(), payment=(), usb=()");
                        })
                )
                
                // Authorization Rules with fine-grained permissions
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/v1/auth/**", "/api/v1/public/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/error", "/favicon.ico").permitAll()
                        
                        // Actuator endpoints - restricted to admin and monitoring
                        .requestMatchers(EndpointRequest.to("health", "info")).permitAll()
                        .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ADMIN")
                        
                        // API endpoints with role-based access
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/analyst/**").hasAnyRole("ADMIN", "ANALYST", "SENIOR_ANALYST")
                        .requestMatchers("/api/v1/coordinator/**").hasAnyRole("ADMIN", "ANALYST", "OPERATOR")
                        .requestMatchers("/api/v1/orchestrator/**").hasAnyRole("ADMIN", "SYSTEM")
                        
                        // Threat detection endpoints
                        .requestMatchers("/api/v1/analyze/**").hasAnyRole("ADMIN", "ANALYST", "OPERATOR")
                        .requestMatchers("/api/v1/intelligence/**").hasAnyRole("ADMIN", "ANALYST")
                        .requestMatchers("/api/v1/response/**").hasAnyRole("ADMIN", "INCIDENT_RESPONDER")
                        
                        // Default deny
                        .anyRequest().authenticated()
                )
                
                // Session Management
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                )
                
                // Exception Handling
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                
                // Custom Filters Chain
                .addFilterBefore(auditLoggingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(threatDetectionFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Argon2 - more secure than BCrypt for enterprise use
        return new Argon2PasswordEncoder(16, 32, 1, 65536, 3);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "https://*.threatdetection.ai",
            "https://localhost:*",
            "https://127.0.0.1:*"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    @Bean
    public CsrfTokenRepository customCsrfTokenRepository() {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookieName("XSRF-TOKEN");
        repository.setHeaderName("X-XSRF-TOKEN");
        repository.setCookiePath("/");
        repository.setCookieMaxAge(3600);
        repository.setSecure(true);
        repository.setSameSite("Strict");
        return repository;
    }

    /**
     * Method-level security expressions for fine-grained access control
     */
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(new CustomPermissionEvaluator());
        return handler;
    }
}
