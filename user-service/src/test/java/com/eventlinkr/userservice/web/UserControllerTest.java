package com.eventlinkr.userservice.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.eventlinkr.userservice.domain.dto.CreateUserRequest;
import com.eventlinkr.userservice.domain.dto.UserProfileUpdateRequest;
import com.eventlinkr.userservice.domain.model.User;
import com.eventlinkr.userservice.service.UserService;

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

    // GET /api/user/{id} - user found
    @Test
    void testGetUserById_Found() {
        User mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setUsername("johndoe");

        when(userService.getUserById(mockUser.getId().toString())).thenReturn(Mono.just(mockUser));

        ResponseEntity<User> response = userController.getUserById(mockUser.getId().toString()).block();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("johndoe", response.getBody().getUsername());
    }

    // GET /api/user/{id} - user not found
    @Test
    void testGetUserById_NotFound() {
        String userId = UUID.randomUUID().toString();
        when(userService.getUserById(userId)).thenReturn(Mono.empty());

        ResponseEntity<User> response = userController.getUserById(userId).block();

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // GET /api/user/me
    @Test
    void testGetCurrentUser() {
        User currentUser = new User();
        currentUser.setUsername("currentuser");

        ResponseEntity<User> response = userController.getCurrentUser(currentUser).block();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("currentuser", response.getBody().getUsername());
    }

    // GET /api/user/search
    @Test
    void testSearchUsers() {
        String query = "john";
        int page = 0;
        int size = 10;
        List<User> users = Arrays.asList(new User(), new User());
        PageImpl<User> userPage = new PageImpl<>(users, PageRequest.of(page, size), users.size());

        when(userService.searchUsers(query, PageRequest.of(page, size))).thenReturn(Mono.just(userPage));

        ResponseEntity<Page<User>> response = userController.searchUsers(query, page, size).block();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Page<User> body = response.getBody();
        assertNotNull(body);
        assertEquals(2, body.getTotalElements());
    }

    // GET /api/user/by-provider - user exists
    @Test
    void findUserByProvider_WhenUserExists_ReturnsOkWithUser() {
        User mockUser = new User();
        mockUser.setFullName("John Doe");

        when(userService.getUserByProviderAndProviderId("linkedin", "123456")).thenReturn(Mono.just(mockUser));

        ResponseEntity<User> response = userController.findUserByProvider("linkedin", "123456").block();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().getFullName());
    }

    // GET /api/user/by-provider - user not found
    @Test
    void findUserByProvider_WhenUserDoesNotExist_ReturnsNotFound() {
        when(userService.getUserByProviderAndProviderId("linkedin", "unknown")).thenReturn(Mono.empty());

        ResponseEntity<User> response = userController.findUserByProvider("linkedin", "unknown").block();

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // POST /api/user - create user success
    @Test
    void testCreateUser_Success() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("newuser");
        request.setEmail("newuser@example.com");
        request.setProvider("google");
        request.setProviderId("google123");

        User createdUser = new User();
        createdUser.setId(UUID.randomUUID());
        createdUser.setUsername("newuser");
        createdUser.setEmail("newuser@example.com");

        when(userService.createUserFromRequest(request)).thenReturn(Mono.just(createdUser));

        ResponseEntity<User> response = userController.createUser(request).block();

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("newuser", response.getBody().getUsername());
    }

    // POST /api/user - create user error
    @Test
    void testCreateUser_Error() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("erroruser");
        request.setEmail("error@example.com");
        request.setProvider("google");
        request.setProviderId("googleError");

        when(userService.createUserFromRequest(request))
                .thenReturn(Mono.error(new IllegalArgumentException("Invalid input")));

        ResponseEntity<User> response = userController.createUser(request).block();

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // PUT /api/user/{id} - update user profile success
    @Test
    void testUpdateUserProfile_Success() {
        String userId = UUID.randomUUID().toString();
        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest();
        updateRequest.setUsername("updatedUser");
        updateRequest.setDisplayName("Updated User");
        updateRequest.setBio("Updated bio");
        updateRequest.setAvatarUrl("http://example.com/avatar.png");

        User updatedUser = new User();
        updatedUser.setId(UUID.fromString(userId));
        updatedUser.setUsername("updatedUser");
        updatedUser.setFullName("Updated User");
        updatedUser.setBio("Updated bio");
        updatedUser.setAvatarUrl("http://example.com/avatar.png");

        when(userService.updateUserProfile(userId, updateRequest)).thenReturn(Mono.just(updatedUser));

        ResponseEntity<User> response = userController.updateUserProfile(userId, updateRequest).block();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("updatedUser", response.getBody().getUsername());
    }

    // PUT /api/user/{id} - update user profile not found
    @Test
    void testUpdateUserProfile_NotFound() {
        String userId = UUID.randomUUID().toString();
        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest();
        updateRequest.setUsername("nonexistent");
        updateRequest.setDisplayName("Nonexistent User");
        updateRequest.setBio("No bio");
        updateRequest.setAvatarUrl("http://example.com/noavatar.png");

        when(userService.updateUserProfile(userId, updateRequest)).thenReturn(Mono.empty());

        ResponseEntity<User> response = userController.updateUserProfile(userId, updateRequest).block();

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // PUT /api/user/{id} - update user profile error
    @Test
    void testUpdateUserProfile_Error() {
        String userId = UUID.randomUUID().toString();
        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest();
        updateRequest.setUsername("errorUser");
        updateRequest.setDisplayName("Error User");
        updateRequest.setBio("Error bio");
        updateRequest.setAvatarUrl("http://example.com/erroravatar.png");

        when(userService.updateUserProfile(userId, updateRequest))
                .thenReturn(Mono.error(new IllegalArgumentException("Invalid input")));

        ResponseEntity<User> response = userController.updateUserProfile(userId, updateRequest).block();

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // DELETE /api/user/{id} - delete user success
    @Test
    void testDeleteUser_Success() {
        String userId = UUID.randomUUID().toString();
        when(userService.deleteUser(userId)).thenReturn(Mono.empty());

        ResponseEntity<Void> response = userController.deleteUser(userId).block();

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    // DELETE /api/user/{id} - delete user error scenario
    @Test
    void testDeleteUser_Error() {
        String userId = UUID.randomUUID().toString();
        when(userService.deleteUser(userId)).thenReturn(Mono.error(new IllegalArgumentException("User not found")));

        try {
            userController.deleteUser(userId).block();
        } catch (Exception e) {
            assertNotNull(e);
        }
    }
}