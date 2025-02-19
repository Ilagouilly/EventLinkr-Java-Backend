package com.eventlinkr.userservice.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;

import com.eventlinkr.userservice.domain.model.User;

import reactor.core.publisher.Mono;

public interface CustomUserRepository extends UserRepository {
    // Add new statistics methods
    @Query("""
                SELECT COUNT(*)
                FROM users
                WHERE created_at >= NOW() - INTERVAL '24 hours'
            """)
    Mono<Long> countUsersCreatedLast24Hours();

    @Query("""
                SELECT COUNT(*)
                FROM users
                WHERE status = :status
                AND updated_at >= NOW() - INTERVAL '7 days'
            """)
    Mono<Long> countUsersByStatusLastWeek(User.UserStatus status);

    // Add user verification method
    @Query("""
                UPDATE users
                SET status = 'ACTIVE',
                    email_verified = true,
                    updated_at = NOW()
                WHERE id = :id
                AND status = 'PENDING_VERIFICATION'
                RETURNING *
            """)
    Mono<User> verifyUser(UUID id);
}