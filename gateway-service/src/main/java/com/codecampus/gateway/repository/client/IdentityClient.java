package com.codecampus.gateway.repository.client;

import com.codecampus.gateway.dto.api.ApiResponse;
import com.codecampus.gateway.dto.request.IntrospectRequest;
import com.codecampus.gateway.dto.response.IntrospectResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

/**
 * Client interface dùng để gọi các API của Identity Service thông qua Spring WebServiceProxy.
 */
public interface IdentityClient {
    /**
     * Gửi yêu cầu kiểm tra (introspect) tính hợp lệ của token đến endpoint của Identity Service.
     *
     * @param request Đối tượng IntrospectRequest chứa token cần xác thực
     * @return Mono chứa ApiResponse với payload là IntrospectResponse từ Identity Service
     */
    @PostExchange(
            url = "/auth/introspect",
            contentType = MediaType.APPLICATION_JSON_VALUE
    )
    Mono<ApiResponse<IntrospectResponse>> introspect(
            @RequestBody IntrospectRequest request);
}
