package com.eventlinkr.userservice.service;

import com.eventlinkr.userservice.domain.dto.CreateUserRequest;
import com.eventlinkr.userservice.domain.model.User;
import com.eventlinkr.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * Creates a new user.
     */
    public Mono<User> createUser(User user) {
        return userRepository.existsByEmail(user.getEmail()).flatMap(exists -> {
            if (exists) {
                return Mono.error(new IllegalArgumentException("Email already exists"));
            }
            // Ensure the ID is null for a new user
            user.setId(null); // Let the database generate the ID
            user.setCreatedAt(Instant.now());
            user.setUpdatedAt(Instant.now());
            user.setStatus(User.UserStatus.PENDING_VERIFICATION); // Set initial status
            return userRepository.save(user); // This should now insert the user
        });
    }

    public Mono<User> createUserFromRequest(CreateUserRequest createUserRequest) {
        User user = User.builder().id(null).username(createUserRequest.getUsername())
                .provider(createUserRequest.getProvider()).providerId(createUserRequest.getProviderId())
                .status(User.UserStatus.PENDING_VERIFICATION).createdAt(Instant.now()).updatedAt(Instant.now()).build();

        return userRepository.save(user);
    }

    /**
     * Handles social authentication user creation/update.
     */
    public Mono<User> upsertSocialUser(User user) {
        return userRepository.upsertSocialUser(user)
                .doOnSuccess(savedUser -> log.info("Upserted social user with ID: {}", savedUser.getId()))
                .doOnError(error -> log.error("Error upserting social user: {}", error.getMessage()));
    }

    /**
     * Finds a user by their ID.
     */
    public Mono<User> findById(UUID id) {
        return userRepository.findById(id).doOnSuccess(user -> log.debug("Found user with ID: {}", id))
                .doOnError(error -> log.error("Error finding user by ID: {}", error.getMessage()));
    }

    /**
     * Finds a user by their Provider and Provider ID.
     */
    public Mono<User> getUserByProviderAndProviderId(String provider, String providerId) {
        return userRepository.findByProviderAndProviderId(provider, providerId);
    }

    /**
     * Updates an existing user's profile.
     */
    public Mono<User> updateUser(UUID id, User userUpdate) {
        return userRepository.findById(id).flatMap(existingUser -> {
            userUpdate.setId(id);
            userUpdate.setCreatedAt(existingUser.getCreatedAt());
            userUpdate.setUpdatedAt(Instant.now());
            userUpdate.setStatus(existingUser.getStatus());
            return userRepository.updateUser(userUpdate);
        }).doOnSuccess(user -> log.info("Updated user with ID: {}", id))
                .doOnError(error -> log.error("Error updating user: {}", error.getMessage()));
    }

    /**
     * Searches for users based on a search term.
     */
    public Flux<User> searchUsers(String searchTerm) {
        return userRepository.searchUsers(searchTerm)
                .doOnComplete(() -> log.debug("Completed user search for term: {}", searchTerm))
                .doOnError(error -> log.error("Error searching users: {}", error.getMessage()));
    }

    /**
     * Changes a user's status.
     */
    public Mono<Void> updateUserStatus(UUID id, User.UserStatus status) {
        return userRepository.updateStatus(id, status)
                .doOnSuccess(v -> log.info("Updated status to {} for user ID: {}", status, id))
                .doOnError(error -> log.error("Error updating user status: {}", error.getMessage()));
    }

    /**
     * Validates if an email is available.
     */
    public Mono<Boolean> isEmailAvailable(String email) {
        return userRepository.existsByEmail(email).map(exists -> !exists);
    }

    /**
     * Validates if a username is available.
     */
    public Mono<Boolean> isUsernameAvailable(String username) {
        return userRepository.existsByUsername(username).map(exists -> !exists);
    }
}