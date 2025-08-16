package com.codecampus.post.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import static com.codecampus.post.constant.exception.ErrorCodeConstant.*;
import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {
    // Các lỗi không phân loại và lỗi chung
    UNCATEGORIZED_EXCEPTION(99999, INTERNAL_SERVER_STATUS,
            "Lỗi chưa phân loại", INTERNAL_SERVER_ERROR),

    // 400 - Bad Request


    // 401 - Unauthorized
    UNAUTHENTICATED(4019001, UNAUTHORIZED_STATUS, "Chưa xác thực!",
            HttpStatus.UNAUTHORIZED),
    AUTHOR_UNAUTHORIZED(4019002,AUTHOR_STATUS, "Không phải người đăng bài",
            HttpStatus.UNAUTHORIZED),

    // 403 - Forbidden
    UNAUTHORIZED(4039001, FORBIDDEN_STATUS, "Bạn không có quyền truy cập!",
            FORBIDDEN),

    // 404 - Not Found
    USER_NOT_FOUND(4049001, NOT_FOUND_STATUS, "Không tìm thấy người dùng!",
            NOT_FOUND),
    TARGET_USER_NOT_FOUND(4049003, NOT_FOUND_STATUS,
            "Không tìm thấy người dùng mục tiêu!", NOT_FOUND),
    POST_NOT_FOUND(4049004, NOT_FOUND_STATUS, "Không tìm thấy bài đăng!",
            NOT_FOUND),
    ORG_NOT_FOUND(4049004, NOT_FOUND_STATUS, "Không tìm thấy tổ chức!",
            NOT_FOUND),


    // 409 - Conflict
    USER_ALREADY_EXISTS(4099001, CONFLICT_STATUS, "Người dùng đã tồn tại!",
            CONFLICT),

    //410 - invalid
    INVALID_FILE_TYPE(4109001, BAD_REQUEST_STATUS, "Định dạng tệp không hợp lệ",
            HttpStatus.BAD_REQUEST)

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
