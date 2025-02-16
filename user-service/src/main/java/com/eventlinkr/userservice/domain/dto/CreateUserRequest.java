package com.eventlinkr.userservice.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {

    @Size(min = 3, max = 50)
    private String username;

    @Email
    private String email;

    @Size(min = 8, max = 100)
    private String password;

    @Size(max = 100)
    private String fullName;

    @Size(min = 8, max = 20)
    private String provider;

    private String providerId;
}