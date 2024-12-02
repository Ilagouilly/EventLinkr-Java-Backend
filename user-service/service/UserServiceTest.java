package com.eventlinkr.userservice.service;

import com.eventlinkr.userservice.domain.dto.UserCreateRequest;
import com.eventlinkr.userservice.domain.mapper.UserMapper;
import com.eventlinkr.userservice.domain.model.User;
import com.eventlinkr.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder, userMapper);
    }

    @Test
    void createUser_WhenEmailNotExists_ShouldCreateUser() {
        // Arrange
        UserCreateRequest createRequest = new UserCreateRequest();
        createRequest.setUsername("testuser");
        createRequest.setEmail("test@example.com");
        createRequest.setPassword("password123");

        User user = User.builder()
            .username("testuser")
            .email("test@example.com")
            .build();

        when(userRepository.existsByEmail(any())).thenReturn(Mono.just(false));
        when(userRepository.existsByUsername(any())).thenReturn(Mono.just(false));
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));

        // Act & Assert
        StepVerifier.create(userService.createUser(createRequest))
            .expectNextCount(1)
            .verifyComplete();
    }

    @Test
    void createUser_WhenEmailExists_ShouldThrowException() {
        // Arrange
        UserCreateRequest createRequest = new UserCreateRequest();
        createRequest.setEmail("test@example.com");

        when(userRepository.existsByEmail(any())).thenReturn(Mono.just(true));

        // Act & Assert
        StepVerifier.create(userService.createUser(createRequest))
            .expectError(RuntimeException.class)
            .verify();
    }
}
