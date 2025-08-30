package com.codecampus.post.repository.httpClient;

import com.codecampus.post.dto.common.ApiResponse;
import com.codecampus.post.dto.response.ProfileResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "profile-service",
    url = "${app.services.profile}",
    path = "/internal")
public interface ProfileServiceClient {
  @GetMapping("/user/{userId}")
  ApiResponse<ProfileResponseDto> internalGetUserProfileByUserId(
      @PathVariable("userId") String userId);
}
