package com.eventlinkr.userservice.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserProfileUpdateRequest(
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    String firstName,

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    String lastName,

    @Email(message = "Invalid email format")
    String email,

    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    String bio
) {}