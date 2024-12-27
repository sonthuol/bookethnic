package com.ethnicdev.identity.service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ethnicdev.identity.constant.PredefinedRole;
import com.ethnicdev.identity.dto.request.ProfileCreationRequest;
import com.ethnicdev.identity.dto.request.UserCreationRequest;
import com.ethnicdev.identity.dto.request.UserUpdateRequest;
import com.ethnicdev.identity.dto.response.UserProfileResponse;
import com.ethnicdev.identity.dto.response.UserResponse;
import com.ethnicdev.identity.entity.Role;
import com.ethnicdev.identity.entity.User;
import com.ethnicdev.identity.exception.AppException;
import com.ethnicdev.identity.exception.ErrorCode;
import com.ethnicdev.identity.mapper.ProfileMapper;
import com.ethnicdev.identity.mapper.UserMapper;
import com.ethnicdev.identity.repository.RoleRepository;
import com.ethnicdev.identity.repository.UserRepository;
import com.ethnicdev.identity.repository.httpclient.ProfileClient;

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
    ProfileMapper profileMapper;
    PasswordEncoder passwordEncoder;
    ProfileClient profileClient;

    public UserResponse createUser(UserCreationRequest request) {

        // Check user exists
        if (this.userRepository.existsByUsername(request.getUsername())) {
            // If user exists then throw exception
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        // Convert to user entity
        User user = this.userMapper.toUser(request);

        // Set password after encode
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Set role
        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);
        user.setRoles(roles);

        // Save user
        user = this.userRepository.save(user);

        // Convert to profile request
        ProfileCreationRequest profileCreationRequest = this.profileMapper.toProfileCreationRequest(request);
        profileCreationRequest.setUserId(user.getId());
        UserProfileResponse userProfileResponse = this.profileClient.createProfile(profileCreationRequest);
        log.info(userProfileResponse.toString());
        return this.userMapper.toUserResponse(user);
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
