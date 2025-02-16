package com.eventlinkr.userservice.web;

import com.eventlinkr.userservice.domain.dto.CreateUserRequest;
import com.eventlinkr.userservice.domain.model.User;
import com.eventlinkr.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Management", description = "Endpoints for managing user data")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/by-provider")
    @Operation(summary = "Find user by provider and provider ID", description = "Retrieves user details based on the authentication provider and provider ID")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    public Mono<ResponseEntity<User>> findUserByProvider(@RequestParam("provider") String provider,
            @RequestParam("provider-id") String providerId) {
        return userService.getUserByProviderAndProviderId(provider, providerId).map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided details")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public Mono<ResponseEntity<User>> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        return userService.createUserFromRequest(createUserRequest)
                .map(savedUser -> ResponseEntity.status(HttpStatus.CREATED).body(savedUser))
                .onErrorResume(IllegalArgumentException.class, e -> Mono.just(ResponseEntity.badRequest().build()));
    }
}