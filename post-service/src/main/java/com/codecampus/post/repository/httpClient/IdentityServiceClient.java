package com.codecampus.post.repository.httpClient;

import com.codecampus.post.config.feign_config.AuthenticationRequestInterceptor;
import com.codecampus.post.config.feign_config.FeignMultipartConfiguration;
import com.codecampus.post.dto.common.ApiResponse;
import com.codecampus.post.dto.request.IntrospectRequest;
import com.codecampus.post.dto.response.IntrospectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "identity-client",
    url = "${app.services.identity}",
    path = "/auth",
    configuration = {FeignMultipartConfiguration.class,
        AuthenticationRequestInterceptor.class})
public interface IdentityServiceClient {
  @PostMapping(
      value = "/introspect",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  ApiResponse<IntrospectResponse> introspect(
      @RequestBody IntrospectRequest request);
}
