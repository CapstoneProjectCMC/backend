package com.codecampus.gateway.exception;

import static com.codecampus.gateway.constant.exception.ErrorCodeConstant.FORBIDDEN_STATUS;
import static com.codecampus.gateway.constant.exception.ErrorCodeConstant.INTERNAL_SERVER_STATUS;

import static com.codecampus.gateway.constant.exception.ErrorCodeConstant.UNAUTHORIZED_STATUS;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
  // Các lỗi không phân loại và lỗi chung
  UNCATEGORIZED_EXCEPTION(99999, INTERNAL_SERVER_STATUS,
      "Uncategorized error", INTERNAL_SERVER_ERROR),

  // 400 - Bad Request

  // 401 - Unauthorized
  UNAUTHENTICATED(40101, UNAUTHORIZED_STATUS, "Unauthenticated!",
                  org.springframework.http.HttpStatus.UNAUTHORIZED),

  // 403 - Forbidden
  UNAUTHORIZED(40301, FORBIDDEN_STATUS, "You do not have permission!",
               FORBIDDEN),

  // 404 - Not Found

  // 409 - Conflict

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
