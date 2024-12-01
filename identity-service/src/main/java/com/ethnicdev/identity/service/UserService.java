package com.ethnicdev.identity.service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ethnicdev.identity.dto.request.UserCreationRequest;
import com.ethnicdev.identity.dto.request.UserUpdateRequest;
import com.ethnicdev.identity.dto.response.UserResponse;
import com.ethnicdev.identity.entity.User;
import com.ethnicdev.identity.enums.Role;
import com.ethnicdev.identity.exception.AppException;
import com.ethnicdev.identity.exception.ErrorCode;
import com.ethnicdev.identity.mapper.UserMapper;
import com.ethnicdev.identity.repository.RoleRepository;
import com.ethnicdev.identity.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreationRequest request) {
        log.info("Service: Create user");
        if (this.userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User user = this.userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
        // user.setRoles(roles);
        return this.userMapper.toUserResponse(this.userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        log.info("In method get Users");
        return this.userRepository.findAll().stream()
                .map(this.userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUser(String userId) {
        log.info("In method get User by id");
        return this.userMapper.toUserResponse(
                this.userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")));
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();
        User user = this.userRepository
                .findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_EXISTED));
        return this.userMapper.toUserResponse(user);
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = this.userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        this.userMapper.updateUser(user, request);
        user.setPassword(this.passwordEncoder.encode(request.getPassword()));
        var roles = this.roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));
        return this.userMapper.toUserResponse(this.userRepository.save(user));
    }

    public void deleteUser(String userId) {
        this.userRepository.deleteById(userId);
    }
}
