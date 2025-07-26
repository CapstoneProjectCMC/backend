package com.codecampus.gateway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import static com.codecampus.gateway.constant.exception.ErrorCodeConstant.FORBIDDEN_STATUS;
import static com.codecampus.gateway.constant.exception.ErrorCodeConstant.INTERNAL_SERVER_STATUS;
import static com.codecampus.gateway.constant.exception.ErrorCodeConstant.TOO_MANY_REQUESTS_STATUS;
import static com.codecampus.gateway.constant.exception.ErrorCodeConstant.UNAUTHORIZED_STATUS;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Định nghĩa các mã lỗi và thông điệp tương ứng.
 */
@Getter
public enum ErrorCode {
    // Các lỗi không phân loại và lỗi chung
    UNCATEGORIZED_EXCEPTION(99999, INTERNAL_SERVER_STATUS,
            "Lỗi chưa phân loại", INTERNAL_SERVER_ERROR),

    // 400 - Bad Request

    // 401 - Unauthorized
    UNAUTHENTICATED(4018801, UNAUTHORIZED_STATUS, "Chưa xác thực!",
            org.springframework.http.HttpStatus.UNAUTHORIZED),

    // 403 - Forbidden
    UNAUTHORIZED(4038801, FORBIDDEN_STATUS, "Bạn không có quyền truy cập!",
            FORBIDDEN),

    // 404 - Not Found

    // 409 - Conflict

    // 429 - Too Many Requests
    RATE_LIMIT_EXCEEDED(4298801, TOO_MANY_REQUESTS_STATUS,
            "Vượt quá giới hạn request", HttpStatus.TOO_MANY_REQUESTS);


    // 500 - Internal Server Error

    ;

    private final int code;
    private final String status;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(
            int code,
            String status,
            String message,
            HttpStatusCode statusCode) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.statusCode = statusCode;
    }
}
