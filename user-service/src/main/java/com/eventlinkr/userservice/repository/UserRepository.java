package com.eventlinkr.userservice.repository;

import com.eventlinkr.userservice.domain.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, UUID> {
    // Existing methods
    Mono<User> findByEmail(String email);

    Mono<User> findByUsername(String username);

    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByUsername(String username);

    Mono<User> findByProviderAndProviderId(String provider, String providerId);

    Flux<User> findByStatus(User.UserStatus status);

    // Updated search method with pagination
    @Query("""
                SELECT * FROM users
                WHERE (:term IS NULL) OR (
                    LOWER(full_name) LIKE LOWER(concat('%', :term, '%'))
                    OR LOWER(email) LIKE LOWER(concat('%', :term, '%'))
                    OR LOWER(username) LIKE LOWER(concat('%', :term, '%'))
                )
                ORDER BY created_at DESC
                LIMIT :#{#pageable.pageSize} OFFSET :#{#pageable.offset}
            """)
    Flux<User> searchUsers(String term, Pageable pageable);

    // Count total results for pagination
    @Query("""
                SELECT COUNT(*) FROM users
                WHERE (:term IS NULL) OR (
                    LOWER(full_name) LIKE LOWER(concat('%', :term, '%'))
                    OR LOWER(email) LIKE LOWER(concat('%', :term, '%'))
                    OR LOWER(username) LIKE LOWER(concat('%', :term, '%'))
                )
            """)
    Mono<Long> countSearchResults(String term);

    // Updated update method
    @Query("""
                UPDATE users
                SET username = :#{#user.username},
                    email = :#{#user.email},
                    full_name = :#{#user.fullName},
                    bio = :#{#user.bio},
                    headline = :#{#user.headline},
                    profile_link = :#{#user.profileLink},
                    headshot = :#{#user.avatarUrl},
                    updated_at = NOW()
                WHERE id = :#{#user.id}
                RETURNING *
            """)
    Mono<User> updateUser(User user);

    // Soft delete
    @Query("""
                UPDATE users
                SET status = 'DELETED',
                    updated_at = NOW()
                WHERE id = :id
            """)
    Mono<Void> softDeleteUser(UUID id);
}