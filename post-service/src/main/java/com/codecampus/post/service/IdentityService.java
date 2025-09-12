package com.codecampus.post.service;

import com.codecampus.post.dto.request.IntrospectRequest;
import com.codecampus.post.dto.response.IntrospectResponse;
import com.codecampus.post.repository.httpClient.IdentityServiceClient;
import feign.FeignException;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityService {
  IdentityServiceClient identityServiceClient;

  public IntrospectResponse introspect(IntrospectRequest request) {
    try {
      var res = identityServiceClient.introspect(request).getResult();
      if (Objects.isNull(res)) {
        return IntrospectResponse.builder().valid(false).build();
      }
      return res;
    } catch (FeignException e) {
      log.error("Introspect failed: {}", e.getMessage(), e);
      return IntrospectResponse.builder().valid(false).build();
    }
  }
}