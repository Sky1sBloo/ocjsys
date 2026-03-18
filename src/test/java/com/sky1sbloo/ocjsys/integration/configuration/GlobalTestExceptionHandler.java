package com.sky1sbloo.ocjsys.integration.configuration;

import com.sky1sbloo.ocjsys.exception.GlobalExceptionHandler;
import com.sky1sbloo.ocjsys.exception.dto.ErrorResponseDto;
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
@Profile("test")
public class GlobalTestExceptionHandler extends GlobalExceptionHandler {
    @Override
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleOtherExceptions(Exception ex) throws Exception {
        if (ex instanceof ResponseStatusException
                || ex instanceof AccessDeniedException
                || ex instanceof HttpRequestMethodNotSupportedException) {
            throw ex;
        }
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto(ex.getMessage()));
    }
}
