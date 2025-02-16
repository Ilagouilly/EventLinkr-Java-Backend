package com.eventlinkr.userservice.domain.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class User {
    @Id
    private UUID id;

    @Size(min = 3, max = 50)
    private String username;

    @Email
    private String email;

    @Column("password_hash")
    private String passwordHash;

    @Column("full_name")
    @Size(max = 100)
    private String fullName;

    @Size(max = 160)
    private String headline;

    @Column("profile_link")
    @Size(max = 255)
    private String profileLink;

    @Size(max = 255)
    private String headshot;

    @Builder.Default
    private UserStatus status = UserStatus.PENDING_VERIFICATION;

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
        ACTIVE, INACTIVE, SUSPENDED, DELETED, PENDING_VERIFICATION
    }
}