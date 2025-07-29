package com.codecampus.identity.dto.response.authentication;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
// Mặc định cho phép response cả null khi Dev
// Khi build thì KHÔNG response null
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    String id;
    String username;
    String email;
    Set<RoleResponse> roles;
    boolean enabled;
}
