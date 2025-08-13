package com.furkanerd.hr_management_system.exception.handler;

import com.furkanerd.hr_management_system.exception.DepartmentNotFoundException;
import com.furkanerd.hr_management_system.exception.PositionNotFoundException;
import com.furkanerd.hr_management_system.model.dto.response.ErrorResponseDto;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(PositionNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handlePositionNotFoundException(PositionNotFoundException e) {
        log.error("Position not found: {}" , e.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(e.getMessage())
                .path("api/v1/positions")
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DepartmentNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleDepartmentNotFoundException(DepartmentNotFoundException e) {
        log.error("Department not found: {}" , e.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(e.getMessage())
                .path("api/v1/departments")
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Validation Exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(MethodArgumentNotValidException e) {
        log.error("Validation Exception: {}" , e.getMessage());

        Map<String,String> errors = e.getBindingResult().getFieldErrors()
                .stream().collect(Collectors.toMap(FieldError::getField,FieldError::getDefaultMessage,(msg1,msg2) -> msg2));

        ErrorResponseDto error = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Exception")
                .message("Input validation error")
                .validationErrors(errors)
                .build();
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Constraint Validation Exceptions
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(ConstraintViolationException e) {
        log.error("Validation Exception: {}" , e.getMessage());

        Map<String,String> errors = new HashMap<>();
        e.getConstraintViolations().forEach(cv->{
            String fieldName = cv.getPropertyPath().toString();
            String message = cv.getMessage();
            errors.put(fieldName,message);
        });

        ErrorResponseDto error = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Constraint Violation Exception")
                .message("Data validation error")
                .validationErrors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex) {
        log.error("Unexpected error: ", ex);

        ErrorResponseDto error = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
