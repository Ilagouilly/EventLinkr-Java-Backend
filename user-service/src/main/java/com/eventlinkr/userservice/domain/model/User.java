package com.eventlinkr.userservice.domain.model;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class User {
    @Id
    private UUID id;

    @NotNull
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotNull
    @Email(message = "Invalid email format")
    private String email;

    @Column("password_hash")
    private String passwordHash;

    @Column("full_name")
    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    private String fullName;

    @Size(max = 160, message = "Headline cannot exceed 160 characters")
    private String headline;

    @Column("profile_link")
    @Size(max = 255, message = "Profile link cannot exceed 255 characters")
    private String profileLink;

    @Column("avatar_url")
    @Size(max = 255, message = "Avatar URL cannot exceed 255 characters")
    private String avatarUrl;

    @Size(max = 255, message = "Headshot URL cannot exceed 255 characters")
    private String headshot;

    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    @Column("bio")
    private String bio;

    @NotNull
    @Setter
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

    @Column("last_login_at")
    private Instant lastLoginAt;

    @Column("email_verified")
    private Boolean emailVerified = false;

    @Column("login_attempts")
    private Integer loginAttempts = 0;

    @Column("last_failed_login")
    private Instant lastFailedLogin;

    public enum UserStatus {
        ACTIVE, INACTIVE, SUSPENDED, DELETED, PENDING_VERIFICATION
    }
}