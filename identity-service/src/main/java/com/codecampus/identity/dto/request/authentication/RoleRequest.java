package com.codecampus.identity.dto.request.authentication;

import com.codecampus.identity.dto.response.authentication.PermissionResponse;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest
{
  String name;
  String description;
  Set<PermissionResponse> permissions;
}
