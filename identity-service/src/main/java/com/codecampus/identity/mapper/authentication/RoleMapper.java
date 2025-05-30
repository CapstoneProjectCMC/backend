package com.codecampus.identity.mapper.authentication;

import com.codecampus.identity.dto.request.authentication.RoleRequest;
import com.codecampus.identity.dto.response.authentication.RoleResponse;
import com.codecampus.identity.entity.account.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper
{
  @Mapping(target = "permissions", ignore = true)
  Role toRole(RoleRequest request);

  RoleResponse toRoleResponse(Role role);
}
