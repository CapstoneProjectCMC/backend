package com.codecampus.identity.controller.authentication;

import com.codecampus.identity.dto.api.ApiResponse;
import com.codecampus.identity.dto.request.authentication.PermissionRequest;
import com.codecampus.identity.dto.response.authentication.PermissionResponse;
import com.codecampus.identity.service.account.PermissionService;
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
public class PermissionController
{
  PermissionService permissionService;

  @PostMapping("/permission")
  ApiResponse<PermissionResponse> createPermission(
      @RequestBody PermissionRequest request) {
    return ApiResponse.<PermissionResponse>builder()
        .result(permissionService.createPermission(request))
        .message("Permission created")
        .build();
  }

  @GetMapping("/permissions")
  ApiResponse<List<PermissionResponse>> getAllPermissions() {
    return ApiResponse.<List<PermissionResponse>>builder()
        .result(permissionService.getAllPermissions())
        .message("Successfully retrieved all permissions")
        .build();
  }

  @DeleteMapping("/permission/{permission}")
  ApiResponse<Void> deletePermission(
      @PathVariable("permission") String permission) {
    permissionService.deletePermission(permission);
    return ApiResponse.<Void>builder()
        .message("Permission deleted")
        .build();
  }
}
