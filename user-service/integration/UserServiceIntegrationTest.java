package com.eventlinkr.userservice.integration;

import com.eventlinkr.userservice.domain.dto.UserCreateRequest;
import com.eventlinkr.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.Duration;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void createUserWithValidData() {
        UserCreateRequest request = new UserCreateRequest(
            "test@example.com", 
            "SecurePassword123!", 
            "John", 
            "Doe"
        );

        StepVerifier.create(userService.createUser(request))
            .expectNextMatches(user -> {
                return user.email().equals(request.email()) &&
                       user.firstName().equals(request.firstName());
            })
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void preventDuplicateUserCreation() {
        UserCreateRequest initialUser = new UserCreateRequest(
            "unique@example.com", 
            "SecurePassword123!", 
            "Jane", 
            "Doe"
        );

        StepVerifier.create(
            userService.createUser(initialUser)
                .then(userService.createUser(initialUser))
        )
            .expectError()
            .verify(Duration.ofSeconds(5));
    }
}