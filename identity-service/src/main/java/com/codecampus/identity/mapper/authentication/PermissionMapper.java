package com.codecampus.identity.mapper.authentication;

import com.codecampus.identity.dto.request.authentication.PermissionRequest;
import com.codecampus.identity.dto.response.authentication.PermissionResponse;
import com.codecampus.identity.entity.account.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
  Permission toPermission(PermissionRequest permissionRequest);

  PermissionResponse toPermissionResponse(Permission permission);
}
