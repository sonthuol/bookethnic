package com.ethnicdev.identity.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.ethnicdev.identity.dto.request.PermissionRequest;
import com.ethnicdev.identity.dto.response.PermissionResponse;
import com.ethnicdev.identity.entity.Permission;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PermissionMapper {

    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
