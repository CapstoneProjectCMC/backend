package com.codecampus.profile.exception;

import static com.codecampus.profile.constant.exception.ErrorCodeConstant.BAD_REQUEST_STATUS;
import static com.codecampus.profile.constant.exception.ErrorCodeConstant.CONFLICT_STATUS;
import static com.codecampus.profile.constant.exception.ErrorCodeConstant.FORBIDDEN_STATUS;
import static com.codecampus.profile.constant.exception.ErrorCodeConstant.INTERNAL_SERVER_STATUS;
import static com.codecampus.profile.constant.exception.ErrorCodeConstant.NOT_FOUND_STATUS;
import static com.codecampus.profile.constant.exception.ErrorCodeConstant.UNAUTHORIZED_STATUS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
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
      "Uncategorized error", INTERNAL_SERVER_ERROR),

  // 400 - Bad Request


  // 401 - Unauthorized
  UNAUTHENTICATED(40101, UNAUTHORIZED_STATUS, "Unauthenticated!",
                  HttpStatus.UNAUTHORIZED),

  // 403 - Forbidden
  UNAUTHORIZED(40301, FORBIDDEN_STATUS, "You do not have permission!",
               FORBIDDEN),

  // 404 - Not Found
  USER_NOT_FOUND(40401, NOT_FOUND_STATUS, "User not found!", NOT_FOUND),


  // 409 - Conflict
  USER_ALREADY_EXISTS(40901, CONFLICT_STATUS, "User already exists",
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
