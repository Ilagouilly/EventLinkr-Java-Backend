package com.eventlinkr.userservice.web;

import com.eventlinkr.userservice.domain.dto.UserCreateRequest;
import com.eventlinkr.userservice.domain.dto.UserDto;
import com.eventlinkr.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(UserController.class)
class UserControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    @Test
    void createUser_ValidRequest_ReturnsCreatedUser() {
        // Arrange
        UserCreateRequest createRequest = new UserCreateRequest();
        createRequest.setUsername("testuser");
        createRequest.setEmail("test@example.com");
        createRequest.setPassword("password123");

        UserDto userDto = UserDto.builder()
            .id(UUID.randomUUID())
            .username("testuser")
            .email("test@example.com")
            .build();

        when(userService.createUser(any(UserCreateRequest.class)))
            .thenReturn(Mono.just(userDto));

        // Act & Assert
        webTestClient.post()
            .uri("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(createRequest)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.username").isEqualTo("testuser")
            .jsonPath("$.email").isEqualTo("test@example.com");
    }
}
