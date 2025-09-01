package com.codecampus.notification.service;

import com.codecampus.notification.dto.request.IntrospectRequest;
import com.codecampus.notification.dto.response.IntrospectResponse;
import com.codecampus.notification.repository.client.IdentityClient;
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
  IdentityClient identityClient;

  public IntrospectResponse introspect(IntrospectRequest req) {
    try {
      IntrospectResponse res = identityClient.introspect(req).getResult();
      if (Objects.isNull(res)) {
        return IntrospectResponse.builder().valid(false).build();
      }
      return res;
    } catch (FeignException ex) {
      log.error("Introspect failed: {}", ex.getMessage(), ex);
      return IntrospectResponse.builder().valid(false).build();
    }
  }
}