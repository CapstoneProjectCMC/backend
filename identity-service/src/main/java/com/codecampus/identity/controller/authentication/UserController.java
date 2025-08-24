package com.codecampus.identity.controller.authentication;

import com.codecampus.identity.dto.common.ApiResponse;
import com.codecampus.identity.dto.common.PageResponse;
import com.codecampus.identity.dto.request.authentication.PasswordCreationRequest;
import com.codecampus.identity.dto.request.authentication.UserCreationRequest;
import com.codecampus.identity.dto.request.authentication.UserUpdateRequest;
import com.codecampus.identity.dto.response.authentication.UserResponse;
import com.codecampus.identity.service.account.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
public class UserController {
  UserService userService;

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/user")
  ApiResponse<Void> createUser(
      @RequestBody @Valid UserCreationRequest request) {
    userService.createUser(request);
    return ApiResponse.<Void>builder()
        .message("Create User successful")
        .build();
  }

  @PostMapping("/user/create-password")
  ApiResponse<Void> createPassword(
      @RequestBody @Valid PasswordCreationRequest request) {
    userService.createPassword(request);
    return ApiResponse.<Void>builder()
        .message(
            "Password has been created, you could use it to log-in")
        .build();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/users")
  ApiResponse<PageResponse<UserResponse>> getUsers(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "size", defaultValue = "10") int size) {
    return ApiResponse.<PageResponse<UserResponse>>builder()
        .result(userService.getUsers(page, size))
        .message("Get Users successful")
        .build();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/user/{userId}")
  ApiResponse<UserResponse> getUser(
      @PathVariable("userId") String userId) {
    return ApiResponse.<UserResponse>builder()
        .result(userService.getUser(userId))
        .message("Get User successful")
        .build();
  }

  @GetMapping("/user/my-info")
  ApiResponse<UserResponse> getMyInfo() {
    return ApiResponse.<UserResponse>builder()
        .result(userService.getMyInfo())
        .message("Get My Info successful")
        .build();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/user/{userId}")
  ApiResponse<String> deleteUser(
      @PathVariable String userId) {
    userService.deleteUser(userId);
    return ApiResponse.<String>builder()
        .result("User deleted successful")
        .build();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/user/{userId}")
  ApiResponse<Void> updateUser(
      @PathVariable("userId") String userId,
      @RequestBody UserUpdateRequest request) {
    userService.updateUserById(userId, request);
    return ApiResponse.<Void>builder()
        .message("Update User successful")
        .build();
  }

  @PutMapping("/user/my-info")
  ApiResponse<Void> updateMyInfo(
      UserUpdateRequest request) {
    userService.updateMyInfo(request);
    return ApiResponse.<Void>builder()
        .message("Update My Info successful")
        .build();
  }
}
