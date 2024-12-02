package com.eventlinkr.userservice.integration;

import com.eventlinkr.userservice.domain.dto.UserCreateRequest;
import com.eventlinkr.userservice.domain.dto.UserDto;
import com.eventlinkr.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

@SpringBootTest
@Testcontainers
public class UserIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://" + 
            postgres.getHost() + ":" + 
            postgres.getFirstMappedPort() + 
            "/" + postgres.getDatabaseName());
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
    }

    @Test
    void createUser_ValidRequest_PersistsUser() {
        UserCreateRequest createRequest = new UserCreateRequest();
        createRequest.setUsername("integrationuser");
        createRequest.setEmail("integration@test.com");
        createRequest.setPassword("password123");

        UserDto createdUser = webTestClient.post()
            .uri("/api/users")
            .bodyValue(createRequest)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(UserDto.class)
            .returnResult()
            .getResponseBody();

        // Verify user was persisted
        StepVerifier.create(userRepository.findByEmail("integration@test.com"))
            .expectNextMatches(user -> user.getEmail().equals("integration@test.com"))
            .verifyComplete();
    }
}
