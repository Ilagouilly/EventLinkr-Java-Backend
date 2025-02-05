package com.eventlinkr.userservice.repository;

import com.eventlinkr.userservice.domain.model.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Repository interface for User entity operations using R2DBC.
 * Provides reactive operations for user management, including social authentication support.
 */
public interface CustomUserRepository extends ReactiveCrudRepository<User, UUID> {
    /**
     * Finds a user by their email address.
     * @param email The email address to search for
     * @return A Mono containing the user if found, empty otherwise
     */
    Mono<User> findByEmail(String email);

    /**
     * Finds a user by their username.
     * @param username The username to search for
     * @return A Mono containing the user if found, empty otherwise
     */
    Mono<User> findByUsername(String username);

    /**
     * Checks if a user exists with the given email address.
     * @param email The email address to check
     * @return A Mono containing true if the email exists, false otherwise
     */
    Mono<Boolean> existsByEmail(String email);

    /**
     * Checks if a user exists with the given username.
     * @param username The username to check
     * @return A Mono containing true if the username exists, false otherwise
     */
    Mono<Boolean> existsByUsername(String username);

    /**
     * Searches for users based on a search term across multiple fields.
     * Performs case-insensitive search on full name, email, and username.
     * Results are ordered by creation date (newest first).
     *
     * @param searchTerm The term to search for
     * @return A Flux of users matching the search criteria
     */
    @Query("""
        SELECT * FROM users 
        WHERE LOWER(full_name) LIKE LOWER(concat('%', :searchTerm, '%')) 
        OR LOWER(email) LIKE LOWER(concat('%', :searchTerm, '%'))
        OR LOWER(username) LIKE LOWER(concat('%', :searchTerm, '%'))
        ORDER BY created_at DESC
    """)
    Flux<User> searchUsers(String searchTerm);

    /**
     * Finds a user by their social provider and provider-specific ID.
     * @param provider The authentication provider (e.g., "google", "facebook")
     * @param providerId The provider-specific user ID
     * @return A Mono containing the user if found, empty otherwise
     */
    Mono<User> findByProviderAndProviderId(String provider, String providerId);

    /**
     * Retrieves all users for a specific authentication provider.
     * @param provider The authentication provider to filter by
     * @return A Flux of users registered with the specified provider
     */
    Flux<User> findByProvider(String provider);

    /**
     * Finds all users with a specific status.
     * @param status The UserStatus to filter by
     * @return A Flux of users with the specified status
     */
    Flux<User> findByStatus(User.UserStatus status);

    /**
     * Counts the number of users with a specific status.
     * @param status The UserStatus to count
     * @return A Mono containing the count of users with the specified status
     */
    Mono<Long> countByStatus(User.UserStatus status);

    /**
     * Retrieves users registered in the last 30 days.
     * Results are ordered by creation date (newest first).
     * 
     * @return A Flux of recently registered users
     */
    @Query("SELECT * FROM users WHERE created_at > NOW() - INTERVAL '30 days' ORDER BY created_at DESC")
    Flux<User> findRecentlyRegisteredUsers();

    /**
     * Performs an upsert operation for a social authentication user.
     * If the user exists (matched by provider and providerId), updates their information.
     * If the user doesn't exist, creates a new user record.
     * 
     * Updates the following fields on conflict:
     * - email
     * - full_name
     * - headline
     * - profile_link
     * - headshot
     * - updated_at
     *
     * @param user The user entity to upsert
     * @return A Mono containing the created or updated user
     */
    @Query("""
        INSERT INTO users (
            id, username, email, full_name, headline, profile_link, 
            headshot, status, provider, provider_id, 
            created_at, updated_at
        ) 
        VALUES (
            COALESCE(:#{#user.id}, gen_random_uuid()), 
            :#{#user.username}, 
            :#{#user.email}, 
            :#{#user.fullName}, 
            :#{#user.headline}, 
            :#{#user.profileLink}, 
            :#{#user.headshot}, 
            :#{#user.status}::user_status, 
            :#{#user.provider}, 
            :#{#user.providerId}, 
            COALESCE(:#{#user.createdAt}, NOW()), 
            NOW()
        )
        ON CONFLICT (provider, provider_id) 
        WHERE provider IS NOT NULL AND provider_id IS NOT NULL
        DO UPDATE SET
            email = EXCLUDED.email,
            full_name = EXCLUDED.full_name,
            headline = EXCLUDED.headline,
            profile_link = EXCLUDED.profile_link,
            headshot = EXCLUDED.headshot,
            updated_at = NOW()
        RETURNING *
    """)
    Mono<User> upsertSocialUser(User user);

    /**
     * Performs a soft delete on a user by updating their status to DELETED.
     * This preserves the user record while marking it as inactive.
     *
     * @param id The UUID of the user to soft delete
     * @return A Mono completing when the operation is finished
     */
    @Query("UPDATE users SET status = 'DELETED', updated_at = NOW() WHERE id = :id")
    Mono<Void> softDeleteUser(UUID id);

    /**
     * Deactivates users who have been in PENDING_VERIFICATION status for more than 24 hours.
     * This helps clean up incomplete registrations and temporary accounts.
     *
     * @return A Mono completing when the operation is finished
     */
    @Query("""
        UPDATE users 
        SET status = 'INACTIVE', updated_at = NOW() 
        WHERE status = 'PENDING_VERIFICATION' 
        AND created_at < NOW() - INTERVAL '24 hours'
    """)
    Mono<Void> deactivateExpiredPendingUsers();
}