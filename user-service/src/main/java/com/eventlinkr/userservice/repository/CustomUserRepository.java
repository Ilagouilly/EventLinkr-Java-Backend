package com.eventlinkr.userservice.repository;

import com.eventlinkr.userservice.domain.model.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomUserRepository extends ReactiveCrudRepository<User, Long> {
    @Query("SELECT * FROM users u WHERE u.email ILIKE :searchTerm OR u.first_name ILIKE :searchTerm OR u.last_name ILIKE :searchTerm")
    Flux<User> searchUsersByNameOrEmail(String searchTerm);

    @Query("SELECT * FROM users u WHERE u.created_at > NOW() - INTERVAL '30 days'")
    Flux<User> findRecentlyRegisteredUsers();

    Mono<Boolean> existsByEmail(String email);
}