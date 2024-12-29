package com.ethnicdev.identity.service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

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

/**
 * User service.
 *
 * @author Thuol-S
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {

    UserMapper userMapper;

    ProfileClient profileClient;

    ProfileMapper profileMapper;

    UserRepository userRepository;

    RoleRepository roleRepository;

    PasswordEncoder passwordEncoder;

    /**
     * Create new user.
     *
     * @param request User request
     * @return User response
     */
    @Transactional
    public UserResponse createUser(UserCreationRequest request) {

        // Check user exists
        if (this.userRepository.existsByUsername(request.getUsername())) {
            // If user exists is throw exception
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        // Convert to user entity
        User user = this.userMapper.toUser(request);

        // Set password after encode
        String passwordEncoded = passwordEncoder.encode(request.getPassword());
        user.setPassword(passwordEncoded);

        // Set role
        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);
        user.setRoles(roles);

        // Save user
        user = this.userRepository.save(user);

        // Convert to profile request
        ProfileCreationRequest profileCreationRequest = this.profileMapper.toProfileCreationRequest(request);

        // Set id of user after register
        profileCreationRequest.setUserId(user.getId());

        // Call api in profile service
        UserProfileResponse userProfileResponse = this.profileClient.createProfile(profileCreationRequest);

        // Display log
        log.info(userProfileResponse.toString());

        return this.userMapper.toUserResponse(user);
    }

    /**
     * Get all user.
     *
     * @return Information of users
     */
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        return this.userRepository.findAll().stream()
                .map(this.userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get user by id.
     *
     * @param userId Id of user
     * @return User response
     */
    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUser(String userId) {
        return this.userMapper.toUserResponse(
                this.userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")));
    }

    /**
     * Get my info of user.
     *
     * @return Information of user
     */
    public UserResponse getMyInfo() {

        // Get context
        var context = SecurityContextHolder.getContext();

        // Get username
        String username = context.getAuthentication().getName();

        // Get user by username
        User user = this.userRepository
                .findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_EXISTED));

        // Convert to user response
        return this.userMapper.toUserResponse(user);
    }

    /**
     * Update user by id.
     *
     * @param userId Id of user
     * @param request User request
     * @return Information of user after updated
     */
    public UserResponse updateUser(String userId, UserUpdateRequest request) {

        // Get user by id
        User user = this.userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Convert to user entity
        this.userMapper.updateUser(user, request);

        // Set passwrod
        String passwordEncoded = this.passwordEncoder.encode(request.getPassword());
        user.setPassword(passwordEncoded);

        // Set role
        var roles = this.roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        // Convert to user response
        return this.userMapper.toUserResponse(this.userRepository.save(user));
    }

    /**
     * Delete user by id.
     *
     * @param userId Id of user
     */
    public void deleteUser(String userId) {
        this.userRepository.deleteById(userId);
    }
}
