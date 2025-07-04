package com.codecampus.profile.exception;

import static com.codecampus.profile.constant.exception.ErrorCodeConstant.CONFLICT_STATUS;
import static com.codecampus.profile.constant.exception.ErrorCodeConstant.FORBIDDEN_STATUS;
import static com.codecampus.profile.constant.exception.ErrorCodeConstant.INTERNAL_SERVER_STATUS;
import static com.codecampus.profile.constant.exception.ErrorCodeConstant.NOT_FOUND_STATUS;
import static com.codecampus.profile.constant.exception.ErrorCodeConstant.UNAUTHORIZED_STATUS;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode
{
  // Các lỗi không phân loại và lỗi chung
  UNCATEGORIZED_EXCEPTION(99999, INTERNAL_SERVER_STATUS,
      "Lỗi chưa phân loại", INTERNAL_SERVER_ERROR),

  // 400 - Bad Request


  // 401 - Unauthorized
  UNAUTHENTICATED(4018101, UNAUTHORIZED_STATUS, "Chưa xác thực!",
      HttpStatus.UNAUTHORIZED),

  // 403 - Forbidden
  UNAUTHORIZED(4038101, FORBIDDEN_STATUS, "Bạn không có quyền truy cập!",
      FORBIDDEN),

  // 404 - Not Found
  USER_NOT_FOUND(4048101, NOT_FOUND_STATUS, "Không tìm thấy người dùng!",
      NOT_FOUND),
  EXERCISE_NOT_FOUND(4048102, NOT_FOUND_STATUS, "Không tìm thấy bài tập!",
      NOT_FOUND),
  TARGET_USER_NOT_FOUND(4048103, NOT_FOUND_STATUS,
      "Không tìm thấy người dùng mục tiêu!", NOT_FOUND),
  POST_NOT_FOUND(4048104, NOT_FOUND_STATUS, "Không tìm thấy bài đăng!",
      NOT_FOUND),
  ORG_NOT_FOUND(4048104, NOT_FOUND_STATUS, "Không tìm thấy tổ chức!",
      NOT_FOUND),
  FILE_NOT_FOUND(4048105, NOT_FOUND_STATUS, "Không tìm thấy file!",
      NOT_FOUND),


  // 409 - Conflict
  USER_ALREADY_EXISTS(4098101, CONFLICT_STATUS, "Người dùng đã tồn tại!",
      CONFLICT),

  ;

  private final int code;
  private final String status;
  private final String message;
  private final HttpStatusCode statusCode;

  ErrorCode(
      int code,
      String status,
      String message,
      HttpStatusCode statusCode)
  {
    this.code = code;
    this.status = status;
    this.message = message;
    this.statusCode = statusCode;
  }
}
