package com.codecampus.payment.repository.client;

import com.codecampus.payment.config.feign.FeignConfigForm;
import com.codecampus.payment.dto.common.ApiResponse;
import com.codecampus.payment.dto.response.UserProfileResponse;
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