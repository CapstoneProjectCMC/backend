package com.codecampus.identity.configuration.config;

import com.codecampus.identity.dto.common.ApiResponse;
import com.codecampus.identity.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * Entry point cho Spring Security khi phát hiện yêu cầu chưa được xác thực.
 * Trả về HTTP 401 Unauthorized cùng payload JSON mô tả lỗi.
 */
public class JwtAuthenticationEntryPoint
        implements AuthenticationEntryPoint {
    /**
     * Phương thức được gọi khi có AuthenticationException xảy ra.
     * Thiết lập HTTP status, content type và trả về body JSON chứa mã lỗi và thông điệp.
     *
     * @param request       đối tượng HttpServletRequest của client
     * @param response      đối tượng HttpServletResponse để gửi phản hồi
     * @param authException ngoại lệ xác thực gây ra việc gọi entry point này
     * @throws IOException nếu ghi response gặp lỗi I/O
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException {
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;

        response.setStatus(errorCode.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        response.getWriter()
                .write(objectMapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}
