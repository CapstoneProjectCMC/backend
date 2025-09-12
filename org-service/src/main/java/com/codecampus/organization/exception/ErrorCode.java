package com.codecampus.organization.exception;

import static com.codecampus.organization.constant.exception.ErrorCodeConstant.BAD_REQUEST_STATUS;
import static com.codecampus.organization.constant.exception.ErrorCodeConstant.CONFLICT_STATUS;
import static com.codecampus.organization.constant.exception.ErrorCodeConstant.FORBIDDEN_STATUS;
import static com.codecampus.organization.constant.exception.ErrorCodeConstant.INTERNAL_SERVER_STATUS;
import static com.codecampus.organization.constant.exception.ErrorCodeConstant.NOT_FOUND_STATUS;
import static com.codecampus.organization.constant.exception.ErrorCodeConstant.UNAUTHORIZED_STATUS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
  // Các lỗi không phân loại và lỗi chung
  UNCATEGORIZED_EXCEPTION(99999, INTERNAL_SERVER_STATUS,
      "Lỗi chưa phân loại", INTERNAL_SERVER_ERROR),

  // 400 - Bad Request
  INVALID_OTP(4009201, BAD_REQUEST_STATUS, "OTP không hợp lệ!", BAD_REQUEST),
  OTP_EXPIRED(4009202, BAD_REQUEST_STATUS, "OTP đã hết hạn!", BAD_REQUEST),
  EMAIL_SEND_FAILED(4009203, BAD_REQUEST_STATUS, "Gửi email thất bại!",
      BAD_REQUEST),
  ACCOUNT_NOT_ACTIVATED(4009204, BAD_REQUEST_STATUS,
      "Tài khoản chưa được kích hoạt!", BAD_REQUEST),
  INVALID_REQUEST_MEMBER(4009205, BAD_REQUEST_STATUS,
      "User đã có ở tổ chức khác!", BAD_REQUEST),


  // 401 - Unauthorized
  UNAUTHENTICATED(4019201, UNAUTHORIZED_STATUS, "Chưa xác thực!",
      HttpStatus.UNAUTHORIZED),
  INVALID_CREDENTIALS(4019202, UNAUTHORIZED_STATUS,
      "Thông tin đăng nhập không hợp lệ!",
      HttpStatus.UNAUTHORIZED),
  INVALID_TOKEN(4019203, UNAUTHORIZED_STATUS, "Token không hợp lệ!",
      HttpStatus.UNAUTHORIZED),
  TOKEN_REVOKED(4019204, UNAUTHORIZED_STATUS, "Token đã bị thu hồi!",
      HttpStatus.UNAUTHORIZED),

  // 403 - Forbidden
  UNAUTHORIZED(40301, FORBIDDEN_STATUS, "Bạn không có quyền truy cập!",
      FORBIDDEN),

  // 404 - Not Found
  USER_NOT_FOUND(4049201, NOT_FOUND_STATUS, "Không tìm thấy người dùng!",
      NOT_FOUND),
  ROLE_NOT_FOUND(4049202, NOT_FOUND_STATUS, "Không tìm thấy vai trò!",
      NOT_FOUND),
  OTP_NOT_FOUND(4049203, NOT_FOUND_STATUS, "Không tìm thấy OTP!", NOT_FOUND),
  EMAIL_NOT_FOUND(4049204, NOT_FOUND_STATUS, "Không tìm thấy email!",
      NOT_FOUND),
  MEMBERSHIP_NOT_FOUND(4049204, NOT_FOUND_STATUS,
      "Không tìm thấy member trong tổ chức!", NOT_FOUND),
  GRADE_NOT_FOUND(4049205, NOT_FOUND_STATUS, "Không tìm thấy khối!",
      NOT_FOUND),
  ORGANIZATION_NOT_FOUND(4049206, NOT_FOUND_STATUS,
      "Không tìm thấy tổ chức!", NOT_FOUND),


  // 409 - Conflict
  USER_ALREADY_EXISTS(4099201, CONFLICT_STATUS, "Người dùng đã tồn tại!",
      CONFLICT),
  EMAIL_ALREADY_EXISTS(4099202, CONFLICT_STATUS, "Email đã tồn tại!",
      CONFLICT),
  PASSWORD_ALREADY_EXISTS(4099203, CONFLICT_STATUS, "Mật khẩu đã tồn tại!",
      CONFLICT),
  DUPLICATED_BLOCK_NAME(4099204, CONFLICT_STATUS,
      "Tên block bị trùng trong tổ chức!",
      CONFLICT),

  // 500 - Internal Server Error
  FAILED_GENERATE_TOKEN(5009201, INTERNAL_SERVER_STATUS,
      "Lỗi tạo JWT token!", INTERNAL_SERVER_ERROR),
  FAILED_VALIDATE_TOKEN(5009202, INTERNAL_SERVER_STATUS,
      "Lỗi xác thực token!", INTERNAL_SERVER_ERROR),
  FAILED_SEND_EMAIL(5009203, INTERNAL_SERVER_STATUS,
      "Lỗi không gửi được email!", INTERNAL_SERVER_ERROR),

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