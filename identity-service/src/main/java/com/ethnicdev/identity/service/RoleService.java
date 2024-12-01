package com.ethnicdev.identity.service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ethnicdev.identity.dto.request.RoleRequest;
import com.ethnicdev.identity.dto.response.RoleResponse;
import com.ethnicdev.identity.mapper.RoleMapper;
import com.ethnicdev.identity.repository.PermissionRepository;
import com.ethnicdev.identity.repository.RoleRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {

    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    public RoleResponse create(RoleRequest request) {
        var role = this.roleMapper.toRole(request);
        var permissions = this.permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));
        role = this.roleRepository.save(role);
        return this.roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAll() {
        return this.roleRepository.findAll().stream()
                .map(this.roleMapper::toRoleResponse)
                .collect(Collectors.toList());
    }

    public void delete(String role) {
        this.roleRepository.deleteById(role);
    }
}
