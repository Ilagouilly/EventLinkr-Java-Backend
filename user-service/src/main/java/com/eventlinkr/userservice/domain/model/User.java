package com.eventlinkr.userservice.domain.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
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
    
    @Column("full_name")
    private String fullName;
    
    private String headline;
    
    @Column("profile_link")
    private String profileLink;
    
    private String headshot;
    
    private UserStatus status;

    private String provider;
    
    @Column("provider_id")
    private String providerId;
    
    @Column("created_at")
    private Instant createdAt;
    
    @Column("updated_at")
    private Instant updatedAt;
    
    @Column("guest_expiration")
    private Instant guestExpiration;

    public enum UserStatus {
        ACTIVE, 
        INACTIVE, 
        SUSPENDED, 
        DELETED,
        PENDING_VERIFICATION
    }
}