package com.codecampus.quiz.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import static com.codecampus.quiz.constant.exception.ErrorCodeConstant.BAD_REQUEST_STATUS;
import static com.codecampus.quiz.constant.exception.ErrorCodeConstant.CONFLICT_STATUS;
import static com.codecampus.quiz.constant.exception.ErrorCodeConstant.FORBIDDEN_STATUS;
import static com.codecampus.quiz.constant.exception.ErrorCodeConstant.INTERNAL_SERVER_STATUS;
import static com.codecampus.quiz.constant.exception.ErrorCodeConstant.NOT_FOUND_STATUS;
import static com.codecampus.quiz.constant.exception.ErrorCodeConstant.UNAUTHORIZED_STATUS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
public enum ErrorCode {
    // Các lỗi không phân loại và lỗi chung
    UNCATEGORIZED_EXCEPTION(99999, INTERNAL_SERVER_STATUS,
            "Lỗi chưa phân loại", INTERNAL_SERVER_ERROR),

    // 400 - Bad Request
    INVALID_OTP(4008401, BAD_REQUEST_STATUS, "OTP không hợp lệ!", BAD_REQUEST),
    OTP_EXPIRED(4008402, BAD_REQUEST_STATUS, "OTP đã hết hạn!", BAD_REQUEST),
    EMAIL_SEND_FAILED(4008403, BAD_REQUEST_STATUS, "Gửi email thất bại!",
            BAD_REQUEST),
    ACCOUNT_NOT_ACTIVATED(4008404, BAD_REQUEST_STATUS,
            "Tài khoản chưa được kích hoạt!", BAD_REQUEST),


    // 401 - Unauthorized
    UNAUTHENTICATED(4018401, UNAUTHORIZED_STATUS, "Chưa xác thực!",
            HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS(4018402, UNAUTHORIZED_STATUS,
            "Thông tin đăng nhập không hợp lệ!",
            HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(4018403, UNAUTHORIZED_STATUS, "Token không hợp lệ!",
            HttpStatus.UNAUTHORIZED),
    TOKEN_REVOKED(4018404, UNAUTHORIZED_STATUS, "Token đã bị thu hồi!",
            HttpStatus.UNAUTHORIZED),

    // 403 - Forbidden
    UNAUTHORIZED(4038401, FORBIDDEN_STATUS, "Bạn không có quyền truy cập!",
            FORBIDDEN),
    EXERCISE_FORBIDDEN(4038402, FORBIDDEN_STATUS,
            "Bạn không có quyền truy cập bài tập này!",
            FORBIDDEN),

    // 404 - Not Found
    USER_NOT_FOUND(4048401, NOT_FOUND_STATUS, "Không tìm thấy người dùng!",
            NOT_FOUND),
    ROLE_NOT_FOUND(4048402, NOT_FOUND_STATUS, "Không tìm thấy vai trò!",
            NOT_FOUND),
    OTP_NOT_FOUND(4048403, NOT_FOUND_STATUS, "Không tìm thấy OTP!", NOT_FOUND),
    EMAIL_NOT_FOUND(4048404, NOT_FOUND_STATUS, "Không tìm thấy email!",
            NOT_FOUND),
    EXERCISE_NOT_FOUND(4048405, NOT_FOUND_STATUS, "Không tìm thấy bài tập!",
            NOT_FOUND),
    QUESTION_NOT_FOUND(4048406, NOT_FOUND_STATUS, "Không tìm thấy câu hỏi!",
            NOT_FOUND),
    ASSIGNMENT_NOT_FOUND(4048407, NOT_FOUND_STATUS,
            "Không tìm thấy bài được giao!", NOT_FOUND),


    // 409 - Conflict
    USER_ALREADY_EXISTS(4098401, CONFLICT_STATUS, "Người dùng đã tồn tại!",
            CONFLICT),
    EMAIL_ALREADY_EXISTS(4098402, CONFLICT_STATUS, "Email đã tồn tại!",
            CONFLICT),
    PASSWORD_ALREADY_EXISTS(4098403, CONFLICT_STATUS, "Mật khẩu đã tồn tại!",
            CONFLICT),
    EXERCISE_DUPLICATED(4098404, CONFLICT_STATUS, "Bài tập đã tồn tại!",
            CONFLICT),

    // 500 - Internal Server Error
    FAILED_GENERATE_TOKEN(5008401, INTERNAL_SERVER_STATUS,
            "Lỗi tạo JWT token!", INTERNAL_SERVER_ERROR),
    FAILED_VALIDATE_TOKEN(5008402, INTERNAL_SERVER_STATUS,
            "Lỗi xác thực token!", INTERNAL_SERVER_ERROR),


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