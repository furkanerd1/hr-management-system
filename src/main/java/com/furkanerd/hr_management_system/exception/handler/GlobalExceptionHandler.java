package com.furkanerd.hr_management_system.exception.handler;

import com.furkanerd.hr_management_system.exception.base.HrManagementException;
import com.furkanerd.hr_management_system.model.dto.response.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final ExceptionResponseBuilder responseBuilder;

    public GlobalExceptionHandler(ExceptionResponseBuilder responseBuilder) {
        this.responseBuilder = responseBuilder;
    }

    @ExceptionHandler(HrManagementException.class)
    public ResponseEntity<ErrorResponseDto> handleHrException(HrManagementException e, HttpServletRequest request) {
        log.error("HR Exception [{}]: {}", e.getErrorCode(), e.getMessage(), e);

        ErrorResponseDto error = responseBuilder.buildErrorResponse(
                e.getHttpStatus(),
                e.getErrorCode(),
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(e.getHttpStatus()).body(error);
    }

    // Spring Security Exceptions

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleBadCredentialsException(BadCredentialsException e, HttpServletRequest request) {
        log.error("Authentication failed: {}", e.getMessage());

        ErrorResponseDto error = responseBuilder.buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "INVALID_CREDENTIALS",
                "Invalid email or password",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthorizationDenied(AuthorizationDeniedException e, HttpServletRequest request) {
        log.error("Authorization denied: {}", e.getMessage());

        ErrorResponseDto error = responseBuilder.buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "ACCESS_DENIED",
                "You do not have permission to access this resource",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUsernameNotFound(UsernameNotFoundException e, HttpServletRequest request) {
        log.error("User not found: {}", e.getMessage());

        ErrorResponseDto error = responseBuilder.buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "USER_NOT_FOUND",
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Validation Exceptions

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("Validation Exception: {}", e.getMessage());

        Map<String, String> errors = e.getBindingResult().getFieldErrors()
                .stream().collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (msg1, msg2) -> msg2));

        ErrorResponseDto error = responseBuilder.buildValidationErrorResponse(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Input validation failed",
                request.getRequestURI(),
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolationException(
            ConstraintViolationException e, HttpServletRequest request) {
        log.error("Constraint Violation Exception: {}", e.getMessage());

        Map<String, String> errors = new HashMap<>();
        e.getConstraintViolations().forEach(cv -> {
            String fieldName = cv.getPropertyPath().toString();
            String message = cv.getMessage();
            errors.put(fieldName, message);
        });

        ErrorResponseDto error = responseBuilder.buildValidationErrorResponse(
                HttpStatus.BAD_REQUEST,
                "CONSTRAINT_VIOLATION",
                "Data validation failed",
                request.getRequestURI(),
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


    // Generic Exception Handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception e, HttpServletRequest request) {
        log.error("Unexpected error: ", e);

        ErrorResponseDto error = responseBuilder.buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
