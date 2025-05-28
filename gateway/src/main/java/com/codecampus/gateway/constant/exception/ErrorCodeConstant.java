package com.codecampus.gateway.constant.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class ErrorCodeConstant {
    public static final String INTERNAL_SERVER_STATUS =
            INTERNAL_SERVER_ERROR.toString();
    public static final String BAD_REQUEST_STATUS = BAD_REQUEST.toString();
    public static final String UNAUTHORIZED_STATUS =
            org.springframework.http.HttpStatus.UNAUTHORIZED.toString();
    public static final String FORBIDDEN_STATUS = FORBIDDEN.toString();
    public static final String NOT_FOUND_STATUS = NOT_FOUND.toString();
    public static final String CONFLICT_STATUS = CONFLICT.toString();
}
