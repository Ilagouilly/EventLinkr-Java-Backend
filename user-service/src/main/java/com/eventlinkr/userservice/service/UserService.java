package com.eventlinkr.userservice.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eventlinkr.userservice.domain.dto.CreateUserRequest;
import com.eventlinkr.userservice.domain.dto.UserProfileUpdateRequest;
import com.eventlinkr.userservice.domain.model.User;
import com.eventlinkr.userservice.exception.ResourceNotFoundException;
import com.eventlinkr.userservice.exception.ValidationException;
import com.eventlinkr.userservice.repository.UserRepository;
import com.eventlinkr.userservice.utils.LoggingFormat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private static final int MAX_QUERY_LENGTH = 255;
    private final UserRepository userRepository;

    /**
     * Creates a new user.
     */
    public Mono<User> createUser(User user) {
        return userRepository.existsByEmail(user.getEmail()).flatMap(exists -> {
            if (exists) {
                return Mono.error(new ValidationException("Email already exists"));
            }
            user.setId(null);
            user.setCreatedAt(Instant.now());
            user.setUpdatedAt(Instant.now());
            user.setStatus(User.UserStatus.PENDING_VERIFICATION);
            return userRepository.save(user);
        }).doOnSuccess(savedUser -> log.info(LoggingFormat.INFO_CREATED, "user", savedUser.getId()))
                .doOnError(error -> log.error(LoggingFormat.ERROR_OPERATION, "creating user", error.getMessage()));
    }

    /**
     * Creates a user from a CreateUserRequest.
     */
    public Mono<User> createUserFromRequest(CreateUserRequest createUserRequest) {
        User user = User.builder().id(null).username(createUserRequest.getUsername()).email(createUserRequest.getEmail())
                .provider(createUserRequest.getProvider()).providerId(createUserRequest.getProviderId()).status(User.UserStatus.PENDING_VERIFICATION)
                .createdAt(Instant.now()).updatedAt(Instant.now()).build();

        return userRepository.save(user).doOnSuccess(savedUser -> log.info(LoggingFormat.INFO_CREATED, "user", savedUser.getId()))
                .doOnError(error -> log.error(LoggingFormat.ERROR_OPERATION, "creating user from request", error.getMessage()));
    }

    /**
     * Gets a user by their ID.
     */
    public Mono<User> getUserById(String id) {
        return userRepository.findById(UUID.fromString(id)).switchIfEmpty(Mono.error(new ResourceNotFoundException("User", id)))
                .doOnSuccess(user -> log.info(LoggingFormat.INFO_RETRIEVED, "user", id))
                .doOnError(error -> log.error(LoggingFormat.ERROR_WITH_ID, "retrieving user", id, error.getMessage()));
    }

    /**
     * Updates a user's profile using UserProfileUpdateRequest.
     */
    public Mono<User> updateUserProfile(String id, UserProfileUpdateRequest updateRequest) {
        return userRepository.findById(UUID.fromString(id)).switchIfEmpty(Mono.error(new ResourceNotFoundException("User", id))).flatMap(existingUser -> {
            existingUser.setUsername(updateRequest.getUsername());
            existingUser.setFullName(updateRequest.getDisplayName());
            existingUser.setBio(updateRequest.getBio());
            existingUser.setAvatarUrl(updateRequest.getAvatarUrl());
            existingUser.setUpdatedAt(Instant.now());
            return userRepository.save(existingUser);
        }).doOnSuccess(user -> log.info(LoggingFormat.INFO_UPDATED, "user profile", id))
                .doOnError(error -> log.error(LoggingFormat.ERROR_WITH_ID, "updating user profile", id, error.getMessage()));
    }

    /**
     * Deletes a user by their ID.
     */
    public Mono<Void> deleteUser(String id) {
        UUID userId = UUID.fromString(id); // Avoid redundant parsing
        return userRepository.existsById(userId).flatMap(exists -> exists.booleanValue() // Explicit primitive usage
                ? userRepository.deleteById(userId)
                : Mono.error(new ResourceNotFoundException("User", id))).doOnSuccess(ignored -> log.info(LoggingFormat.INFO_DELETED, "user", id))
                .doOnError(error -> log.error(LoggingFormat.ERROR_WITH_ID, "deleting user", id, error.getMessage()));
    }

    /**
     * Searches users with pagination.
     */
    public Mono<PageImpl<User>> searchUsers(String query, Pageable pageable) {
        try {
            validateSearchParameters(query, pageable);
        } catch (IllegalArgumentException e) {
            return Mono.error(new ValidationException(e.getMessage()));
        }

        String sanitizedQuery = query != null ? query.trim() : "";

        Flux<User> searchResults = sanitizedQuery.isEmpty() ? userRepository.findAll() : userRepository.searchUsers(sanitizedQuery, pageable);

        return searchResults.collectList()
                .map(users -> new PageImpl<>(
                        users.subList((int) pageable.getOffset(), Math.min((int) pageable.getOffset() + pageable.getPageSize(), users.size())), pageable,
                        users.size()))
                .doOnSuccess(page -> log.debug(LoggingFormat.DEBUG_FOUND, page.getTotalElements(), "user search"))
                .doOnError(error -> log.error(LoggingFormat.ERROR_OPERATION, "searching users", error.getMessage()));
    }

    /**
     * Finds a user by their Provider and Provider ID.
     */
    public Mono<User> getUserByProviderAndProviderId(String provider, String providerId) {
        return userRepository.findByProviderAndProviderId(provider, providerId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(String.format("User with provider %s and providerId %s", provider, providerId))))
                .doOnSuccess(user -> log.debug(LoggingFormat.DEBUG_FOUND, "user", String.format("provider: %s, providerId: %s", provider, providerId)))
                .doOnError(error -> log.error(LoggingFormat.ERROR_OPERATION,
                        String.format("finding user by provider: %s and providerId: %s", provider, providerId), error.getMessage()));
    }

    /**
     * Validates if an email is available.
     */
    public Mono<Boolean> isEmailAvailable(String email) {
        return userRepository.existsByEmail(email).map(exists -> !exists).doOnSuccess(
                available -> log.debug(LoggingFormat.DEBUG_PROCESSING, String.format("email availability check for %s: %s", email, available), ""));
    }

    /**
     * Validates if a username is available.
     */
    public Mono<Boolean> isUsernameAvailable(String username) {
        return userRepository.existsByUsername(username).map(exists -> !exists).doOnSuccess(
                available -> log.debug(LoggingFormat.DEBUG_PROCESSING, String.format("username availability check for %s: %s", username, available), ""));
    }

    /**
     * Validates search parameters for user queries.
     */
    private void validateSearchParameters(String query, Pageable pageable) {
        if (pageable == null) {
            throw new ValidationException("Pageable parameter cannot be null");
        }
        if (pageable.getPageSize() <= 0) {
            throw new ValidationException("Page size must be greater than 0");
        }
        if (pageable.getPageNumber() < 0) {
            throw new ValidationException("Page number cannot be negative");
        }
        if (query != null && query.trim().length() > MAX_QUERY_LENGTH) {
            throw new ValidationException("Search query exceeds maximum length");
        }
    }
}
