package com.codecampus.profile.config;

import com.codecampus.profile.dto.common.ApiResponse;
import com.codecampus.profile.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * Entry point tùy chỉnh cho Spring Security khi phát hiện yêu cầu không được xác thực.
 *
 * <p>Khi quá trình xác thực thất bại (ví dụ thiếu token hoặc token không hợp lệ),
 * phương thức {@link #commence} sẽ được gọi để trả về phản hồi HTTP 401 với
 * payload JSON chứa mã lỗi và thông điệp tương ứng.</p>
 */
public class JwtAuthenticationEntryPoint
    implements AuthenticationEntryPoint {
  /**
   * Xử lý phản hồi khi yêu cầu không được xác thực.
   *
   * <p>Thiết lập:
   * <ul>
   *   <li>Status HTTP = 401 (Unauthorized).</li>
   *   <li>Content-Type = application/json.</li>
   *   <li>Body JSON bao gồm:
   *     <ul>
   *       <li>code: mã lỗi từ {@link ErrorCode#UNAUTHENTICATED}.</li>
   *       <li>message: thông điệp lỗi tương ứng.</li>
   *     </ul>
   *   </li>
   * </ul>
   * </p>
   *
   * @param request       đối tượng {@link HttpServletRequest} của client
   * @param response      đối tượng {@link HttpServletResponse} để gửi phản hồi
   * @param authException ngoại lệ xác thực đã xảy ra
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

    response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    response.flushBuffer();
  }
}
