package com.codecampus.post.repository.httpClient;

import com.codecampus.post.config.feign.AuthenticationRequestInterceptor;
import com.codecampus.post.config.feign.FeignConfigForm;
import com.codecampus.post.dto.common.ApiResponse;
import com.codecampus.post.dto.request.IntrospectRequest;
import com.codecampus.post.dto.response.IntrospectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "identity-client",
    url = "${app.services.identity}",
    path = "/auth",
    configuration = {AuthenticationRequestInterceptor.class,
        FeignConfigForm.class}
)
public interface IdentityServiceClient {
  @PostMapping(
      value = "/introspect")
  ApiResponse<IntrospectResponse> introspect(
      @RequestBody IntrospectRequest request);
}
