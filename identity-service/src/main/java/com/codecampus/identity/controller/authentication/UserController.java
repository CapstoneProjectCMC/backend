package com.codecampus.identity.controller.authentication;

import com.codecampus.identity.dto.api.ApiResponse;
import com.codecampus.identity.dto.request.authentication.PasswordCreationRequest;
import com.codecampus.identity.dto.request.authentication.UserCreationRequest;
import com.codecampus.identity.dto.request.authentication.UserUpdateRequest;
import com.codecampus.identity.dto.response.authentication.UserResponse;
import com.codecampus.identity.service.authentication.UserService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
public class UserController
{
  UserService userService;

  @PostMapping("/user")
  ApiResponse<UserResponse> createUser(
      @RequestBody @Valid UserCreationRequest request) {
    return ApiResponse.<UserResponse>builder()
        .result(userService.createUser(request))
        .message("Create User successful")
        .build();
  }

  @PostMapping("/user/create-password")
  ApiResponse<Void> createPassword(
      @RequestBody @Valid PasswordCreationRequest request) {
    userService.createPassword(request);
    return ApiResponse.<Void>builder()
        .message("Password has been created, you could use it to log-in")
        .build();
  }

  @GetMapping("/users")
  ApiResponse<List<UserResponse>> getUsers() {
    return ApiResponse.<List<UserResponse>>builder()
        .result(userService.getUsers())
        .message("Get Users successful")
        .build();
  }

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

  @DeleteMapping("/user/{userId}")
  ApiResponse<String> deleteUser(
      @PathVariable String userId) {
    userService.deleteUser(userId);
    return ApiResponse.<String>builder()
        .result("User deleted successful")
        .build();
  }

  @PutMapping("/user/{userId}")
  ApiResponse<UserResponse> updateUser(
      @PathVariable String userId,
      @RequestBody UserUpdateRequest request) {
    return ApiResponse.<UserResponse>builder()
        .result(userService.updateUser(userId, request))
        .message("Update User successful")
        .build();
  }
}
