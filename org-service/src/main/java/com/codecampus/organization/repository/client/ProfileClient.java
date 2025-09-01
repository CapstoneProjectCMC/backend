package com.codecampus.organization.repository.client;

import com.codecampus.organization.dto.common.ApiResponse;
import com.codecampus.organization.dto.response.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "profile-service",
    url = "${app.services.profile}",
    path = "/internal"
)
public interface ProfileClient {
  @GetMapping("/user/{userId}")
  ApiResponse<UserProfileResponse> internalGetUserProfileByUserId(
      @PathVariable("userId") String userId);
}