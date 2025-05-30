package com.codecampus.identity.exception;

import static com.codecampus.identity.constant.exception.ErrorCodeConstant.BAD_REQUEST_STATUS;
import static com.codecampus.identity.constant.exception.ErrorCodeConstant.CONFLICT_STATUS;
import static com.codecampus.identity.constant.exception.ErrorCodeConstant.FORBIDDEN_STATUS;
import static com.codecampus.identity.constant.exception.ErrorCodeConstant.INTERNAL_SERVER_STATUS;
import static com.codecampus.identity.constant.exception.ErrorCodeConstant.NOT_FOUND_STATUS;
import static com.codecampus.identity.constant.exception.ErrorCodeConstant.UNAUTHORIZED_STATUS;
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
  INVALID_OTP(40001, BAD_REQUEST_STATUS, "Invalid OTP", BAD_REQUEST),
  OTP_EXPIRED(40002, BAD_REQUEST_STATUS, "OTP expired", BAD_REQUEST),
  EMAIL_SEND_FAILED(40003, BAD_REQUEST_STATUS, "Failed to send email", BAD_REQUEST),
  ACCOUNT_NOT_ACTIVATED(40004, BAD_REQUEST_STATUS, "Account not activated", BAD_REQUEST),


  // 401 - Unauthorized
  UNAUTHENTICATED(40101, UNAUTHORIZED_STATUS, "Unauthenticated!",
      HttpStatus.UNAUTHORIZED),
  INVALID_CREDENTIALS(40102, UNAUTHORIZED_STATUS, "Invalid credentials!",
      HttpStatus.UNAUTHORIZED),
  INVALID_TOKEN(40103, UNAUTHORIZED_STATUS, "Invalid token!", HttpStatus.UNAUTHORIZED),
  TOKEN_REVOKED(40104, UNAUTHORIZED_STATUS, "Token has been revoked",
      HttpStatus.UNAUTHORIZED),

  // 403 - Forbidden
  UNAUTHORIZED(40301, FORBIDDEN_STATUS, "You do not have permission!",
      FORBIDDEN),

  // 404 - Not Found
  USER_NOT_FOUND(40401, NOT_FOUND_STATUS, "User not found!", NOT_FOUND),
  ROLE_NOT_FOUND(40402, NOT_FOUND_STATUS, "Role not found!", NOT_FOUND),
  OTP_NOT_FOUND(40403, NOT_FOUND_STATUS, "OTP not found", NOT_FOUND),
  EMAIL_NOT_FOUND(40404, NOT_FOUND_STATUS, "Email not found", NOT_FOUND),


  // 409 - Conflict
  USER_ALREADY_EXISTS(40901, CONFLICT_STATUS, "User already exists",
      CONFLICT),
  EMAIL_ALREADY_EXISTS(40902, CONFLICT_STATUS, "Email already exists",
      CONFLICT),

  // 500 - Internal Server Error
  FAILED_GENERATE_TOKEN(50001, INTERNAL_SERVER_STATUS,
      "Error generating JWT token!", INTERNAL_SERVER_ERROR),
  FAILED_VALIDATE_TOKEN(50002, INTERNAL_SERVER_STATUS,
      "Token validation error!", INTERNAL_SERVER_ERROR),


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