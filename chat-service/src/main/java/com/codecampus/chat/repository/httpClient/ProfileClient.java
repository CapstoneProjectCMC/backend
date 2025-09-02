package com.codecampus.chat.repository.httpClient;

import com.codecampus.chat.configuration.feign.FeignConfigForm;
import com.codecampus.chat.dto.common.ApiResponse;
import com.codecampus.chat.dto.response.UserProfileResponse;
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

  @GetMapping(value = "/user/{userId}")
  ApiResponse<UserProfileResponse> getUserProfileByUserId(
      @PathVariable("userId") String userId);
}
