package com.codecampus.chat.repository.httpClient;

import com.codecampus.chat.configuration.config.FeignConfig;
import com.codecampus.chat.dto.common.ApiResponse;
import com.codecampus.chat.dto.response.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "profile-service",
    url = "${app.services.profile}",
    path = "/internal",
    configuration = {FeignConfig.class}
)
public interface ProfileClient {

  @GetMapping(value = "/user/{userId}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  ApiResponse<UserProfileResponse> getUserProfileByUserId(
      @PathVariable("userId") String userId);
}
