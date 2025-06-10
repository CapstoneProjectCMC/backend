package com.codecampus.identity.service.account;

import com.codecampus.identity.dto.request.authentication.PermissionRequest;
import com.codecampus.identity.dto.response.authentication.PermissionResponse;
import com.codecampus.identity.entity.account.Permission;
import com.codecampus.identity.mapper.authentication.PermissionMapper;
import com.codecampus.identity.repository.account.PermissionRepository;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService
{
  PermissionRepository permissionRepository;
  PermissionMapper permissionMapper;

  @PreAuthorize("hasRole('ADMIN')")
  public PermissionResponse createPermission(PermissionRequest request) {
    Permission permission = permissionMapper.toPermission(request);
    permission = permissionRepository.save(permission);
    return permissionMapper.toPermissionResponse(permission);
  }

  @PreAuthorize("hasRole('ADMIN')")
  public List<PermissionResponse> getAllPermissions()
  {
    var permissions = permissionRepository.findAll();
    return permissions.stream()
        .map(permissionMapper::toPermissionResponse)
        .toList();
  }

  @PreAuthorize("hasRole('ADMIN')")
  public void deletePermission(String permission) {
    permissionRepository.deleteById(permission);
  }
}
