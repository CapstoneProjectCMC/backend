package com.codecampus.identity.controller.authentication;

import com.codecampus.identity.dto.common.ApiResponse;
import com.codecampus.identity.dto.request.authentication.RoleRequest;
import com.codecampus.identity.dto.response.authentication.RoleResponse;
import com.codecampus.identity.service.account.RoleService;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
public class RoleController {
  RoleService roleService;

  @PostMapping("/role")
  ApiResponse<RoleResponse> createRole(
      @RequestBody RoleRequest request) {
    return ApiResponse.<RoleResponse>builder()
        .result(roleService.createRole(request))
        .message("Role created")
        .build();
  }

  @GetMapping("/roles")
  ApiResponse<List<RoleResponse>> getAllRoles() {
    return ApiResponse.<List<RoleResponse>>builder()
        .result(roleService.getAllRoles())
        .message("Successfully retrieved all roles")
        .build();
  }

  @DeleteMapping("/role/{role}")
  ApiResponse<Void> deleteRole(
      @PathVariable String role) {
    roleService.delete(role);
    return ApiResponse.<Void>builder()
        .message("Role deleted")
        .build();
  }
}
