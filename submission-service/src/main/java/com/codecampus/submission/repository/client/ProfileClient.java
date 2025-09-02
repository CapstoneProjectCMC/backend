package com.codecampus.submission.repository.client;

import com.codecampus.submission.configuration.feign.FeignConfigForm;
import com.codecampus.submission.dto.common.ApiResponse;
import com.codecampus.submission.dto.response.profile.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "profile-service",
    url = "${app.services.profile}",
    path = "/internal",
    configuration = FeignConfigForm.class
)
public interface ProfileClient {

  @GetMapping("/user/{userId}")
  ApiResponse<UserProfileResponse> internalGetUserProfileByUserId(
      @PathVariable("userId") String userId);
}