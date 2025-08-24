package com.codecampus.chat.repository.httpClient;

import com.codecampus.chat.configuration.config.AuthenticationRequestInterceptor;
import com.codecampus.chat.configuration.config.FeignConfig;
import com.codecampus.chat.dto.common.ApiResponse;
import com.codecampus.chat.dto.request.IntrospectRequest;
import com.codecampus.chat.dto.response.IntrospectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "identity-client",
    url = "${app.services.identity}",
    path = "/auth",
    configuration = {
        AuthenticationRequestInterceptor.class, FeignConfig.class}
)
public interface IdentityClient {

  @PostMapping(
      value = "/introspect",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  ApiResponse<IntrospectResponse> introspect(
      @RequestBody IntrospectRequest request);
}
