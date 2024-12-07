package com.ethnicdev.identity.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.ethnicdev.identity.dto.request.ProfileCreationRequest;
import com.ethnicdev.identity.dto.request.UserCreationRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProfileMapper {

    @Mapping(target = "userId", ignore = true)
    ProfileCreationRequest toProfileCreationRequest(UserCreationRequest request);
}
