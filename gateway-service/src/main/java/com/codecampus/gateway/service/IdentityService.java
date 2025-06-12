package com.codecampus.gateway.service;

import com.codecampus.gateway.dto.api.ApiResponse;
import com.codecampus.gateway.dto.request.IntrospectRequest;
import com.codecampus.gateway.dto.response.IntrospectResponse;
import com.codecampus.gateway.repository.client.IdentityClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityService
{
  IdentityClient identityClient;

  public Mono<ApiResponse<IntrospectResponse>> introspect(
      String token) {
    return identityClient.introspect(
        IntrospectRequest.builder()
            .token(token)
            .build());
  }
}
