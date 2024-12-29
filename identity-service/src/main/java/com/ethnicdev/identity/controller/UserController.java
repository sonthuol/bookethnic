package com.ethnicdev.identity.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ethnicdev.identity.dto.request.UserCreationRequest;
import com.ethnicdev.identity.dto.request.UserUpdateRequest;
import com.ethnicdev.identity.dto.response.ApiResponse;
import com.ethnicdev.identity.dto.response.UserResponse;
import com.ethnicdev.identity.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

/**
 * User controller.
 *
 * @author Thuol-S
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {

    UserService userService;

    /**
     * Register new user.
     *
     * @param request User request
     * @return User response
     */
    @PostMapping("/registration")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        log.info("Controller: Create user");
        return ApiResponse.<UserResponse>builder()
                .result(this.userService.createUser(request))
                .build();
    }

    /**
     * Get all user.
     *
     * @return Information of users
     */
    @GetMapping
    ApiResponse<List<UserResponse>> getUsers() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return ApiResponse.<List<UserResponse>>builder()
                .result(this.userService.getUsers())
                .build();
    }

    /**
     * Get user by id.
     *
     * @param userId Id of user
     * @return Information of user
     */
    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable String userId) {
        return ApiResponse.<UserResponse>builder()
                .result(this.userService.getUser(userId))
                .build();
    }

    /**
     * Get information of user.
     *
     * @return Information of user
     */
    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(this.userService.getMyInfo())
                .build();
    }

    /**
     * Update user by id.
     *
     * @param userId Id of user
     * @param request User request
     * @return Information of user after updated
     */
    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(this.userService.updateUser(userId, request))
                .build();
    }

    /**
     * Delete user by id
     *
     * @param userId Id of user
     * @return Message user deleted
     */
    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable String userId) {
        this.userService.deleteUser(userId);
        return "User has been deleted";
    }
}
