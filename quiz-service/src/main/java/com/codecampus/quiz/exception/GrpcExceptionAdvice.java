package com.codecampus.quiz.exception;

import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.springframework.dao.DataIntegrityViolationException;

@Slf4j
@GrpcAdvice
public class GrpcExceptionAdvice {
    @GrpcExceptionHandler(AppException.class)
    public Status handleApp(AppException ex) {
        // Business error → INVALID_ARGUMENT
        return Status.INVALID_ARGUMENT.withDescription(ex.getMessage());
    }

    @GrpcExceptionHandler(DataIntegrityViolationException.class)
    public Status handleDup(DataIntegrityViolationException ex) {
        return Status.ALREADY_EXISTS.withDescription(
                "ID trùng hoặc ràng buộc DB");
    }

    @GrpcExceptionHandler(Throwable.class)
    public Status handle(Throwable ex) {
        log.error("Lỗi không xác định", ex);
        return Status.UNKNOWN.withDescription("Internal server error");
    }
}
