package com.codecampus.identity.service.authentication;

import com.codecampus.identity.dto.request.authentication.RoleRequest;
import com.codecampus.identity.dto.response.authentication.PermissionResponse;
import com.codecampus.identity.dto.response.authentication.RoleResponse;
import com.codecampus.identity.mapper.authentication.RoleMapper;
import com.codecampus.identity.repository.account.PermissionRepository;
import com.codecampus.identity.repository.account.RoleRepository;
import java.util.HashSet;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService
{
  RoleRepository roleRepository;
  PermissionRepository permissionRepository;
  RoleMapper roleMapper;

  public RoleResponse createRole(RoleRequest roleRequest) {
    var role = roleMapper.toRole(roleRequest);

    var permissions = permissionRepository
        .findAllById(roleRequest.getPermissions().stream().map(PermissionResponse::getName).toList());
    role.setPermissions(new HashSet<>(permissions));

    role = roleRepository.save(role);
    return roleMapper.toRoleResponse(role);
  }

  public List<RoleResponse> getAllRoles()
  {
    return roleRepository.findAll()
        .stream()
        .map(roleMapper::toRoleResponse)
        .toList();
  }

  public void delete(String role) {
    roleRepository.deleteById(role);
  }
}
