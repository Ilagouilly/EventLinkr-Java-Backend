package com.eventlinkr.userservice.repository;

import com.eventlinkr.userservice.domain.model.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Repository interface for managing User entities with reactive operations.
 */
@Repository
public interface UserRepository extends ReactiveCrudRepository<User, UUID> {
    // Basic user lookups
    Mono<User> findByEmail(String email);
    Mono<User> findByUsername(String username);
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByUsername(String username);

    // Social auth related lookups
    Mono<User> findByProviderAndProviderId(String provider, String providerId);
    
    // Status based queries
    Flux<User> findByStatus(User.UserStatus status);

    /**
     * Updates an existing user's profile information.
     */
    @Query("""
        UPDATE users 
        SET full_name = :#{#user.fullName},
            email = :#{#user.email},
            headline = :#{#user.headline},
            profile_link = :#{#user.profileLink},
            headshot = :#{#user.headshot},
            updated_at = NOW()
        WHERE id = :#{#user.id}
        RETURNING *
    """)
    Mono<User> updateUser(User user);

    /**
     * Creates or updates a user from social authentication.
     */
    @Query("""
        INSERT INTO users (
            id, username, email, full_name, headline, 
            profile_link, headshot, status, provider, 
            provider_id, created_at, updated_at
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
     * Updates a user's status.
     */
    @Query("UPDATE users SET status = :status, updated_at = NOW() WHERE id = :id")
    Mono<Void> updateStatus(UUID id, User.UserStatus status);

    /**
     * Searches users by name or email.
     */
    @Query("""
        SELECT * FROM users 
        WHERE LOWER(full_name) LIKE LOWER(concat('%', :term, '%')) 
        OR LOWER(email) LIKE LOWER(concat('%', :term, '%'))
        ORDER BY created_at DESC
    """)
    Flux<User> searchUsers(String term);
}