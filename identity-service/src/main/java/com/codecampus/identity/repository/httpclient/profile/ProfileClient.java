package com.codecampus.identity.repository.httpclient.profile;

import com.codecampus.identity.configuration.config.AuthenticationRequestInterceptor;
import com.codecampus.identity.dto.common.ApiResponse;
import com.codecampus.identity.dto.request.profile.UserProfileCreationRequest;
import com.codecampus.identity.dto.response.profile.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "profile-service",
    url = "${app.services.profile}",
    configuration = {AuthenticationRequestInterceptor.class}
)
public interface ProfileClient {
  @PostMapping(
      value = "/internal/user",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  ApiResponse<UserProfileResponse> createUserProfile(
      @RequestBody UserProfileCreationRequest request);
}
