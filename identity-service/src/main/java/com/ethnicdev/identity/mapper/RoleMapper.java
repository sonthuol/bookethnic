package com.ethnicdev.identity.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.ethnicdev.identity.dto.request.RoleRequest;
import com.ethnicdev.identity.dto.response.RoleResponse;
import com.ethnicdev.identity.entity.Role;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleMapper {

    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
