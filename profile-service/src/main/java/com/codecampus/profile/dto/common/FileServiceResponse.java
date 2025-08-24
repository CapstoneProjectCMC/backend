package com.codecampus.profile.dto.common;

public record FileServiceResponse<T>(
    int code,
    String message,
    String status,
    T result
) {
}
