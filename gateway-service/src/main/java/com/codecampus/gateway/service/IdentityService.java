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

/**
 * Dịch vụ xác thực và kiểm tra tính hợp lệ của token người dùng.
 * Sử dụng IdentityClient để gọi API introspect của Identity Service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityService {
    IdentityClient identityClient;

    /**
     * Gửi yêu cầu kiểm tra tính hợp lệ của token đến Identity Service.
     *
     * @param token chuỗi JWT hoặc token cần xác thực
     * @return Mono chứa ApiResponse với thông tin phản hồi IntrospectResponse từ Identity Service
     */
    public Mono<ApiResponse<IntrospectResponse>> introspect(
            String token) {
        return identityClient.introspect(
                IntrospectRequest.builder()
                        .token(token)
                        .build());
    }
}
