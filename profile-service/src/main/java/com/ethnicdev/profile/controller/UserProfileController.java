package com.ethnicdev.profile.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.ethnicdev.profile.dto.response.UserProfileResponse;
import com.ethnicdev.profile.service.UserProfileService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileController {

    UserProfileService userProfileService;

    @GetMapping("/users")
    List<UserProfileResponse> getAllProfiles() {
        return userProfileService.getAllProfiles();
    }

    @GetMapping("/users/{profileId}")
    UserProfileResponse getProfile(@PathVariable String profileId) {
        return this.userProfileService.getProfile(profileId);
    }
}
