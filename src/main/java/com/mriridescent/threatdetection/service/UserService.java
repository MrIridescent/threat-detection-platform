package com.mriridescent.threatdetection.service;

import com.mriridescent.threatdetection.dto.RegisterRequest;
import com.mriridescent.threatdetection.model.User;
import com.mriridescent.threatdetection.model.Role;
import com.mriridescent.threatdetection.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for user management operations.
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    /**
     * Create a new user
     *
     * @param user The user to create
     * @return The created user
     */
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Create a new user from registration request
     *
     * @param request The registration request
     * @return The created user
     */
    public User createUser(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // Default role for new registrations
                .enabled(true)
                .build();

        return userRepository.save(user);
    }

    /**
     * Get a user by ID
     *
     * @param id The user ID
     * @return The user if found
     * @throws RuntimeException if the user is not found
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    /**
     * Get a user by username
     *
     * @param username The username
     * @return The user if found
     * @throws UsernameNotFoundException if the user is not found
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    /**
     * Get all users
     *
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Update a user
     *
     * @param user The user to update
     * @return The updated user
     */
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Delete a user by ID
     *
     * @param id The user ID to delete
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Check if a username exists
     *
     * @param username The username to check
     * @return true if the username exists, false otherwise
     */
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Check if an email exists
     *
     * @param email The email to check
     * @return true if the email exists, false otherwise
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
