package com.codecampus.identity.mapper.authentication;

import com.codecampus.identity.dto.request.authentication.RoleRequest;
import com.codecampus.identity.dto.response.authentication.RoleResponse;
import com.codecampus.identity.entity.account.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {PermissionMapper.class})
public interface RoleMapper {
  Role toRole(RoleRequest request);

  RoleResponse toRoleResponse(Role role);
}
