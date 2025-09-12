package com.codecampus.payment.exception;

import static com.codecampus.payment.constant.exception.ErrorCodeConstant.BAD_REQUEST_STATUS;
import static com.codecampus.payment.constant.exception.ErrorCodeConstant.CONFLICT_STATUS;
import static com.codecampus.payment.constant.exception.ErrorCodeConstant.FORBIDDEN_STATUS;
import static com.codecampus.payment.constant.exception.ErrorCodeConstant.INTERNAL_SERVER_STATUS;
import static com.codecampus.payment.constant.exception.ErrorCodeConstant.NOT_FOUND_STATUS;
import static com.codecampus.payment.constant.exception.ErrorCodeConstant.UNAUTHORIZED_STATUS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
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
  INVALID_REQUEST(4009101, BAD_REQUEST_STATUS, "Request không hợp lệ!",
      BAD_REQUEST),
  DUPLICATE_TRANSACTION_ID(4009102, BAD_REQUEST_STATUS,
      "Mã giao dịch bị trùng!",
      BAD_REQUEST),
  DUPLICATE_REFERENCE_CODE(4009103, BAD_REQUEST_STATUS,
      "Mã tham chiếu bị trùng!",
      BAD_REQUEST),

  // 401 - Unauthorized
  UNAUTHENTICATED(4019101, UNAUTHORIZED_STATUS, "Chưa xác thực!",
      HttpStatus.UNAUTHORIZED),

  // 403 - Forbidden
  UNAUTHORIZED(4039101, FORBIDDEN_STATUS, "Bạn không có quyền truy cập!",
      FORBIDDEN),

  // 404 - Not Found
  USER_NOT_FOUND(4049101, NOT_FOUND_STATUS, "Không tìm thấy người dùng!",
      NOT_FOUND),
  TARGET_USER_NOT_FOUND(4049103, NOT_FOUND_STATUS,
      "Không tìm thấy người dùng mục tiêu!", NOT_FOUND),
  POST_NOT_FOUND(4049104, NOT_FOUND_STATUS, "Không tìm thấy bài đăng!",
      NOT_FOUND),
  ORG_NOT_FOUND(4049104, NOT_FOUND_STATUS, "Không tìm thấy tổ chức!",
      NOT_FOUND),

  //405 - Payment Error
  WALLET_NOT_FOUND(4059101, NOT_FOUND.toString(), "Không tìm thấy ví!",
      NOT_FOUND),
  INSUFFICIENT_BALANCE(4059102, NOT_ACCEPTABLE.toString(), "Số dư không đủ!",
      BAD_REQUEST),
  PURCHASED_ITEM(4059103, NOT_ACCEPTABLE.toString(), "Đã mua sản phẩm!",
      BAD_REQUEST),

  // 406 - Input Validation Error
  MONTH_INVALID(4069101, BAD_REQUEST_STATUS, "Tháng không hợp lệ!", BAD_REQUEST),

  YEAR_INVALID(4069102, BAD_REQUEST_STATUS, "Năm không hợp lệ!", BAD_REQUEST),

  // 409 - Conflict
  USER_ALREADY_EXISTS(4099101, CONFLICT_STATUS, "Người dùng đã tồn tại!",
      CONFLICT),

  //410 - invalid
  INVALID_FILE_TYPE(4109101, BAD_REQUEST_STATUS, "Định dạng tệp không hợp lệ",
      HttpStatus.BAD_REQUEST);

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
