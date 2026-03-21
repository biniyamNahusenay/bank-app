package com.bank.bankapp.controller;

import com.bank.bankapp.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex,
                                                              HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Bad credentials", request.getRequestURI());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex,
                                                            HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "Access denied", request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .orElse("Validation failed");

        return buildResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex,
                                                       HttpServletRequest request) {
        HttpStatus status = resolveStatus(ex.getMessage());
        return buildResponse(status, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex,
                                                       HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", request.getRequestURI());
    }

    private HttpStatus resolveStatus(String message) {
        if (message == null) {
            return HttpStatus.BAD_REQUEST;
        }
        if (message.contains("not found")) {
            return HttpStatus.NOT_FOUND;
        }
        if (message.contains("Unauthorized")) {
            return HttpStatus.FORBIDDEN;
        }
        if (message.contains("Insufficient funds")) {
            return HttpStatus.BAD_REQUEST;
        }
        if (message.contains("already")) {
            return HttpStatus.CONFLICT;
        }
        return HttpStatus.BAD_REQUEST;
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status,
                                                        String message,
                                                        String path) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );
        return ResponseEntity.status(status).body(errorResponse);
    }
}
