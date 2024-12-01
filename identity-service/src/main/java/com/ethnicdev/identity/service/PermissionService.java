package com.ethnicdev.identity.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ethnicdev.identity.dto.request.PermissionRequest;
import com.ethnicdev.identity.dto.response.PermissionResponse;
import com.ethnicdev.identity.entity.Permission;
import com.ethnicdev.identity.mapper.PermissionMapper;
import com.ethnicdev.identity.repository.PermissionRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {

    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionRequest request) {
        Permission permission = this.permissionMapper.toPermission(request);
        permission = this.permissionRepository.save(permission);
        return this.permissionMapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAll() {
        var permissions = this.permissionRepository.findAll();
        return permissions.stream()
                .map(this.permissionMapper::toPermissionResponse)
                .collect(Collectors.toList());
    }

    public void delete(String permisison) {
        this.permissionRepository.deleteById(permisison);
    }
}
