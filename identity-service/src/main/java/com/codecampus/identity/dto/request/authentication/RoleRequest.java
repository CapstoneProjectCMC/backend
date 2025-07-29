package com.codecampus.identity.dto.request.authentication;

import com.codecampus.identity.dto.response.authentication.PermissionResponse;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest {
    String name;
    String description;
    Set<PermissionResponse> permissions;
}
