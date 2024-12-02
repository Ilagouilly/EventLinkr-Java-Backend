package com.eventlinkr.userservice.web;

import com.eventlinkr.userservice.domain.dto.UserCreateRequest;
import com.eventlinkr.userservice.domain.dto.UserDto;
import com.eventlinkr.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserDto> createUser(@Valid @RequestBody UserCreateRequest createRequest) {
        return userService.createUser(createRequest);
    }

    @GetMapping("/email/{email}")
    public Mono<UserDto> getUserByEmail(@PathVariable String email) {
        return userService.findByEmail(email);
    }
}
