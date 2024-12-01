package com.ethnicdev.profile.service;

import org.springframework.stereotype.Service;

import com.ethnicdev.profile.dto.request.ProfileCreationRequest;
import com.ethnicdev.profile.dto.response.UserProfileResponse;
import com.ethnicdev.profile.entity.UserProfile;
import com.ethnicdev.profile.mapper.UserProfileMapper;
import com.ethnicdev.profile.repository.UserProfileRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserProfileService {

    UserProfileRepository userProfileRepository;

    UserProfileMapper userProfileMapper;

    public UserProfileResponse createProfile(ProfileCreationRequest request) {
        UserProfile entity = this.userProfileMapper.toUserProfile(request);
        entity = this.userProfileRepository.save(entity);
        return this.userProfileMapper.toUserProfileResponse(entity);
    }

    public UserProfileResponse getProfile(String id) {
        UserProfile enity =
                this.userProfileRepository.findById(id).orElseThrow(() -> new RuntimeException("Profile not found."));
        return this.userProfileMapper.toUserProfileResponse(enity);
    }
}
