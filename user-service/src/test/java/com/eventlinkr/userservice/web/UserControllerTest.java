package com.eventlinkr.userservice.web;

import com.eventlinkr.userservice.domain.model.User;
import com.eventlinkr.userservice.service.UserService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController(userService);
    }

    @Test
    void findUserByProvider_WhenUserExists_ReturnsOkWithUser() {
        User mockUser = new User();
        mockUser.setFullName("John Doe");

        when(userService.getUserByProviderAndProviderId("linkedin", "123456")).thenReturn(Mono.just(mockUser));

        Mono<ResponseEntity<User>> response = userController.findUserByProvider("linkedin", "123456");
        ResponseEntity<User> userResponse = response.block();

        assertNotNull(userResponse);
        assertEquals(HttpStatus.OK, userResponse.getStatusCode());
        assertNotNull(userResponse.getBody());
        assertEquals("John Doe", userResponse.getBody().getFullName());
    }

    @Test
    void findUserByProvider_WhenUserDoesNotExist_ReturnsNotFound() {
        when(userService.getUserByProviderAndProviderId("linkedin", "unknown")).thenReturn(Mono.empty());

        Mono<ResponseEntity<User>> response = userController.findUserByProvider("linkedin", "unknown");
        ResponseEntity<User> userResponse = response.block();

        assertNotNull(userResponse);
        assertEquals(HttpStatus.NOT_FOUND, userResponse.getStatusCode());
    }
}
