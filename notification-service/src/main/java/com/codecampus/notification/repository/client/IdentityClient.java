package com.codecampus.notification.repository.client;

import com.codecampus.notification.dto.common.ApiResponse;
import com.codecampus.notification.dto.request.IntrospectRequest;
import com.codecampus.notification.dto.response.IntrospectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "identity-client",
    url = "${app.services.identity}",
    path = "/auth"
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