package com.codecampus.gateway.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppException {
  private ErrorCode errorCode;

  public AppException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
