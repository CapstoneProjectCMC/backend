package com.codecampus.notification.repository.client;

import com.codecampus.notification.configuration.feign.AuthenticationRequestInterceptor;
import com.codecampus.notification.configuration.feign.FeignConfigForm;
import com.codecampus.notification.dto.common.ApiResponse;
import com.codecampus.notification.dto.request.IntrospectRequest;
import com.codecampus.notification.dto.response.IntrospectResponse;
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
public interface IdentityClient {
  @PostMapping(
      value = "/introspect")
  ApiResponse<IntrospectResponse> introspect(
      @RequestBody IntrospectRequest request);
}