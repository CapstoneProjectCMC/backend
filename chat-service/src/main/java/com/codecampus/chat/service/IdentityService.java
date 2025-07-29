package com.codecampus.chat.service;

import com.codecampus.chat.dto.request.IntrospectRequest;
import com.codecampus.chat.dto.response.IntrospectResponse;
import com.codecampus.chat.repository.httpClient.IdentityClient;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityService {

    IdentityClient identityClient;

    public IntrospectResponse introspect(
            IntrospectRequest introspectRequest) {

        try {
            IntrospectResponse introspectResponse = identityClient
                    .introspect(introspectRequest)
                    .getResult();

            if (Objects.isNull(introspectResponse)) {
                return IntrospectResponse.builder()
                        .valid(false)
                        .build();
            }
            return introspectResponse;
        } catch (FeignException exception) {
            log.error("Introspect failed: {}", exception.getMessage(),
                    exception);

            return IntrospectResponse.builder()
                    .valid(false)
                    .build();
        }

    }
}
