package com.eventlinkr.userservice.domain.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@Table("users")
public class User {
    @Id
    private UUID id;
    private String username;
    private String email;
    private String passwordHash;
    private UserStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public enum UserStatus {
        ACTIVE, 
        INACTIVE, 
        SUSPENDED, 
        DELETED
    }
}