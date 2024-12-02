package com.eventlinkr.userservice.service;

import com.eventlinkr.userservice.domain.dto.UserCreateRequest;
import com.eventlinkr.userservice.domain.dto.UserDto;
import com.eventlinkr.userservice.domain.mapper.UserMapper;
import com.eventlinkr.userservice.domain.model.User;
import com.eventlinkr.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public Mono<UserDto> createUser(UserCreateRequest createRequest) {
        return userRepository.existsByEmail(createRequest.getEmail())
            .flatMap(emailExists -> {
                if (Boolean.TRUE.equals(emailExists)) {
                    return Mono.error(new RuntimeException("Email already exists"));
                }
                return userRepository.existsByUsername(createRequest.getUsername());
            })
            .flatMap(usernameExists -> {
                if (Boolean.TRUE.equals(usernameExists)) {
                    return Mono.error(new RuntimeException("Username already exists"));
                }
                
                User newUser = User.builder()
                    .username(createRequest.getUsername())
                    .email(createRequest.getEmail())
                    .passwordHash(passwordEncoder.encode(createRequest.getPassword()))
                    .status(User.UserStatus.ACTIVE)
                    .build();
                
                return userRepository.save(newUser);
            })
            .map(userMapper::userToUserDto);
    }

    public Mono<UserDto> findByEmail(String email) {
        return userRepository.findByEmail(email)
            .map(userMapper::userToUserDto);
    }
}
