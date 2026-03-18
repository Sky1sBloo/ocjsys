package com.sky1sbloo.ocjsys.exception;

import com.sky1sbloo.ocjsys.exception.dto.ErrorResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestControllerAdvice
@Profile("dev")
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> handleNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(CodeRunnerException.class)
    public ResponseEntity<ErrorResponseDto> handleCodeRunner(CodeRunnerException ex) {
        if (ex.getType() == CodeRunnerException.Type.UNSUPPORTED_LANGUAGE) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto(ex.getMessage()));
        }
        log.error("Code Runner Exception", ex);
        if (ex.getType() == CodeRunnerException.Type.INTERRUPTED) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(ex.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleOtherExceptions(Exception ex) throws Exception {
        if (ex instanceof ResponseStatusException
                || ex instanceof AccessDeniedException
                || ex instanceof HttpRequestMethodNotSupportedException) {
            throw ex;
        }
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto("An unexpected error occurred"));
    }
}
