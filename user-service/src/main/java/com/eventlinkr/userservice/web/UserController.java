package com.eventlinkr.userservice.web;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventlinkr.userservice.domain.dto.CreateUserRequest;
import com.eventlinkr.userservice.domain.dto.UserProfileUpdateRequest;
import com.eventlinkr.userservice.domain.model.User;
import com.eventlinkr.userservice.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Management", description = "Endpoints for managing user data")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves user details based on their unique identifier")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    public Mono<ResponseEntity<User>> getUserById(@PathVariable String id) {
        return userService.getUserById(id).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Retrieves the profile of the currently authenticated user")
    @ApiResponse(responseCode = "200", description = "Current user profile retrieved")
    @ApiResponse(responseCode = "401", description = "Not authenticated")
    public Mono<ResponseEntity<User>> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        return Mono.just(ResponseEntity.ok(currentUser));
    }

    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Search users by name, email, or other criteria")
    @ApiResponse(responseCode = "200", description = "Search results retrieved")
    public Mono<ResponseEntity<Page<User>>> searchUsers(@RequestParam(required = false) String query, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return userService.searchUsers(query, PageRequest.of(page, size)).map(ResponseEntity::ok);
    }

    @GetMapping("/by-provider")
    @Operation(summary = "Find user by provider and provider ID", description = "Retrieves user details based on the authentication provider and provider ID")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    public Mono<ResponseEntity<User>> findUserByProvider(@RequestParam("provider") String provider, @RequestParam("provider-id") String providerId) {
        return userService.getUserByProviderAndProviderId(provider, providerId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided details")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public Mono<ResponseEntity<User>> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        return userService.createUserFromRequest(createUserRequest).map(savedUser -> ResponseEntity.status(HttpStatus.CREATED).body(savedUser))
                .onErrorResume(IllegalArgumentException.class, e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user profile", description = "Updates the profile information for a specific user")
    @ApiResponse(responseCode = "200", description = "User profile updated successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public Mono<ResponseEntity<User>> updateUserProfile(@PathVariable String id, @Valid @RequestBody UserProfileUpdateRequest updateRequest) {
        return userService.updateUserProfile(id, updateRequest).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(IllegalArgumentException.class, e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user account permanently")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable String id) {
        return userService.deleteUser(id).then(Mono.just(ResponseEntity.noContent().<Void>build())).defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
