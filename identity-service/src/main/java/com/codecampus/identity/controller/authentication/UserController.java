package com.codecampus.identity.controller.authentication;

import com.codecampus.identity.dto.common.ApiResponse;
import com.codecampus.identity.dto.data.BulkImportResult;
import com.codecampus.identity.dto.request.authentication.ChangePasswordRequest;
import com.codecampus.identity.dto.request.authentication.PasswordCreationRequest;
import com.codecampus.identity.dto.request.authentication.UserCreationRequest;
import com.codecampus.identity.dto.request.authentication.UserUpdateRequest;
import com.codecampus.identity.service.account.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
public class UserController {
  UserService userService;

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/user")
  ApiResponse<Void> createStudent(
      @RequestBody @Valid UserCreationRequest request) {
    userService.createStudent(request);
    return ApiResponse.<Void>builder()
        .message("Create User successful")
        .build();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/teacher")
  ApiResponse<Void> createTeacher(
      @RequestBody @Valid UserCreationRequest request) {
    userService.createTeacher(request);
    return ApiResponse.<Void>builder()
        .message("Create Teacher successful")
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
  @DeleteMapping("/user/{userId}")
  ApiResponse<String> deleteUser(
      @PathVariable String userId) {
    userService.deleteUser(userId);
    return ApiResponse.<String>builder()
        .result("User deleted successful")
        .build();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/user/{userId}")
  ApiResponse<String> restoreUser(
      @PathVariable String userId) {
    userService.restoreUser(userId);
    return ApiResponse.<String>builder()
        .result("User restored successful")
        .build();
  }

  @PatchMapping("/user/password")
  ApiResponse<Void> changeMyPassword(
      @RequestBody ChangePasswordRequest req) {
    userService.changeMyPassword(req);
    return ApiResponse.<Void>builder()
        .message("Password changed")
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

  @PostMapping("/users/import")
  @PreAuthorize("hasRole('ADMIN')")
  public ApiResponse<BulkImportResult> importUsers(
      @RequestPart("file") MultipartFile file) {
    return ApiResponse.<BulkImportResult>builder()
        .message("Imported")
        .result(userService.importUsers(file))
        .build();
  }

  @GetMapping("/users/export")
  @PreAuthorize("hasRole('ADMIN')")
  public void exportUsers(HttpServletResponse response) {
    userService.exportUsers(response);
  }
}
