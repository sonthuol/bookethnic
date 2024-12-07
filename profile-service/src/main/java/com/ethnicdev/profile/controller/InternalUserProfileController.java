package com.ethnicdev.profile.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ethnicdev.profile.dto.request.ProfileCreationRequest;
import com.ethnicdev.profile.dto.response.UserProfileResponse;
import com.ethnicdev.profile.service.UserProfileService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalUserProfileController {

    UserProfileService userProfileService;

    @PostMapping("/internal/users")
    UserProfileResponse postMethodName(@RequestBody ProfileCreationRequest request) {
        return this.userProfileService.createProfile(request);
    }
}
