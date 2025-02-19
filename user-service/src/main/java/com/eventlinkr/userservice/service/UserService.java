package com.eventlinkr.userservice.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eventlinkr.userservice.domain.dto.CreateUserRequest;
import com.eventlinkr.userservice.domain.dto.UserProfileUpdateRequest;
import com.eventlinkr.userservice.domain.model.User;
import com.eventlinkr.userservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
            user.setId(null);
            user.setCreatedAt(Instant.now());
            user.setUpdatedAt(Instant.now());
            user.setStatus(User.UserStatus.PENDING_VERIFICATION);
            return userRepository.save(user);
        }).doOnSuccess(savedUser -> log.info("Created new user with ID: {}", savedUser.getId()))
                .doOnError(error -> log.error("Error creating user: {}", error.getMessage()));
    }

    /**
     * Creates a user from a CreateUserRequest.
     */
    public Mono<User> createUserFromRequest(CreateUserRequest createUserRequest) {
        User user = User.builder().id(null).username(createUserRequest.getUsername())
                .email(createUserRequest.getEmail()).provider(createUserRequest.getProvider())
                .providerId(createUserRequest.getProviderId()).status(User.UserStatus.PENDING_VERIFICATION)
                .createdAt(Instant.now()).updatedAt(Instant.now()).build();

        return userRepository.save(user)
                .doOnSuccess(savedUser -> log.info("Created user from request with ID: {}", savedUser.getId()))
                .doOnError(error -> log.error("Error creating user from request: {}", error.getMessage()));
    }

    /**
     * Gets a user by their ID.
     */
    public Mono<User> getUserById(String id) {
        return userRepository.findById(UUID.fromString(id))
                .doOnSuccess(user -> log.debug("Retrieved user with ID: {}", id))
                .doOnError(error -> log.error("Error retrieving user by ID: {}", error.getMessage()));
    }

    /**
     * Updates a user's profile using UserProfileUpdateRequest.
     */
    public Mono<User> updateUserProfile(String id, UserProfileUpdateRequest updateRequest) {
        return userRepository.findById(UUID.fromString(id)).flatMap(existingUser -> {
            existingUser.setUsername(updateRequest.getUsername());
            existingUser.setFullName(updateRequest.getDisplayName());
            existingUser.setBio(updateRequest.getBio());
            existingUser.setAvatarUrl(updateRequest.getAvatarUrl());
            existingUser.setUpdatedAt(Instant.now());
            return userRepository.save(existingUser);
        }).doOnSuccess(user -> log.info("Updated profile for user ID: {}", id))
                .doOnError(error -> log.error("Error updating user profile: {}", error.getMessage()));
    }

    /**
     * Deletes a user by their ID.
     */
    public Mono<Void> deleteUser(String id) {
        return userRepository.deleteById(UUID.fromString(id))
                .doOnSuccess(void_ -> log.info("Deleted user with ID: {}", id))
                .doOnError(error -> log.error("Error deleting user: {}", error.getMessage()));
    }

    /**
     * Searches users with pagination.
     */
    public Mono<PageImpl<User>> searchUsers(String query, Pageable pageable) {
        Flux<User> searchResults;
        if (query == null || query.trim().isEmpty()) {
            searchResults = userRepository.findAll();
        } else {
            searchResults = userRepository.searchUsers(query, pageable);
        }

        return searchResults.collectList()
                .map(users -> new PageImpl<>(
                        users.subList((int) pageable.getOffset(),
                                Math.min((int) pageable.getOffset() + pageable.getPageSize(), users.size())),
                        pageable, users.size()))
                .doOnSuccess(page -> log.debug("Completed user search with {} results", page.getTotalElements()))
                .doOnError(error -> log.error("Error searching users: {}", error.getMessage()));
    }

    /**
     * Finds a user by their Provider and Provider ID.
     */
    public Mono<User> getUserByProviderAndProviderId(String provider, String providerId) {
        return userRepository.findByProviderAndProviderId(provider, providerId)
                .doOnSuccess(user -> log.debug("Found user with provider: {} and providerId: {}", provider, providerId))
                .doOnError(error -> log.error("Error finding user by provider details: {}", error.getMessage()));
    }

    /**
     * Validates if an email is available.
     */
    public Mono<Boolean> isEmailAvailable(String email) {
        return userRepository.existsByEmail(email).map(exists -> !exists)
                .doOnSuccess(available -> log.debug("Email {} availability checked: {}", email, available));
    }

    /**
     * Validates if a username is available.
     */
    public Mono<Boolean> isUsernameAvailable(String username) {
        return userRepository.existsByUsername(username).map(exists -> !exists)
                .doOnSuccess(available -> log.debug("Username {} availability checked: {}", username, available));
    }
}