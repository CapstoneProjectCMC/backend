package com.codecampus.post.repository.httpClient;

import com.codecampus.post.config.FeignConfig.AuthenticationRequestInterceptor;
import com.codecampus.post.config.FeignConfig.FeignMultipartSupportConfig;
import com.codecampus.post.dto.common.ApiResponse;
import com.codecampus.post.dto.response.ProfileResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "profile-service",
    url = "http://localhost:8081",
    configuration = {FeignMultipartSupportConfig.class,
        AuthenticationRequestInterceptor.class})
public interface ProfileServiceClient {
  @GetMapping(value = "/profile/user/{userId}")
  ApiResponse<ProfileResponseDto> getUserProfileById(
      @PathVariable String userId);
}
