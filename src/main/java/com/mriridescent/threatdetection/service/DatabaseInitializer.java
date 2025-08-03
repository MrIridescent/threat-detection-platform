package com.mriridescent.threatdetection.service;

import com.mriridescent.threatdetection.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Initializes the database with sample data for demonstration purposes.
 */
@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create admin user if it doesn't exist
        if (!userService.usernameExists("admin")) {
            User adminUser = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin"))
                    .firstName("Admin")
                    .lastName("User")
                    .role(User.Role.ADMIN)
                    .build();
            userService.createUser(adminUser);
        }

        // Create analyst user if it doesn't exist
        if (!userService.usernameExists("analyst")) {
            User analystUser = User.builder()
                    .username("analyst")
                    .email("analyst@example.com")
                    .password(passwordEncoder.encode("analyst"))
                    .firstName("Security")
                    .lastName("Analyst")
                    .role(User.Role.ANALYST)
                    .build();
            userService.createUser(analystUser);
        }

        // Create regular user if it doesn't exist
        if (!userService.usernameExists("user")) {
            User regularUser = User.builder()
                    .username("user")
                    .email("user@example.com")
                    .password(passwordEncoder.encode("user"))
                    .firstName("Regular")
                    .lastName("User")
                    .role(User.Role.USER)
                    .build();
            userService.createUser(regularUser);
        }
    }
}
