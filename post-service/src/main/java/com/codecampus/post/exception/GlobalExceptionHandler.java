package com.codecampus.post.exception;

import com.codecampus.post.dto.common.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    /**
     * Xử lý các ngoại lệ RuntimeException không được bắt riêng.
     *
     * @param exception ngoại lệ RuntimeException được ném ra
     * @return ResponseEntity chứa ApiResponse với mã lỗi, thông điệp và trạng thái lỗi tương ứng
     */
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<?>> handlingRuntimeException(
            RuntimeException exception) {
        log.error("Exception: " + exception.getMessage(), exception);

        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
        apiResponse.setStatus(ErrorCode.UNCATEGORIZED_EXCEPTION.getStatus());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    /**
     * Xử lý ngoại lệ ứng dụng (AppException) và chuyển thành ApiResponse.
     *
     * @param exception ngoại lệ AppException được ném ra
     * @return ResponseEntity chứa ApiResponse với thông tin lỗi tương ứng
     */
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<?>> handlingAppException(
            AppException exception) {
        log.error("Exception: " + exception.getMessage(), exception);

        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        apiResponse.setStatus(errorCode.getStatus());

        return ResponseEntity.status(errorCode.getStatusCode())
                .body(apiResponse);
    }

    /**
     * Xử lý ngoại lệ AccessDeniedException khi người dùng không có quyền truy cập.
     *
     * @param exception ngoại lệ AccessDeniedException
     * @return ResponseEntity chứa ApiResponse với thông tin lỗi truy cập
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<?>> handlingAccessDeniedException(
            AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .status(errorCode.getStatus())
                .build();

        return ResponseEntity.status(errorCode.getStatusCode())
                .body(apiResponse);
    }

    @ExceptionHandler(value = FeignException.class)
    ResponseEntity<?> handlingFeignException(FeignException exception) {
        log.error("FeignException: " + exception.getMessage(), exception);

        try {
            // Lấy body lỗi từ service kia (UTF-8)
            String body = exception.contentUTF8();

            // Parse về ApiResponse nếu format giống của mình
            ApiResponse<?> apiResponse = objectMapper.readValue(body, ApiResponse.class);

            return ResponseEntity.status(exception.status())
                    .body(apiResponse);

        } catch (Exception e) {
            // Nếu parse thất bại thì trả body thô
            return ResponseEntity.status(exception.status())
                    .body(exception.contentUTF8());
        }
    }
}
