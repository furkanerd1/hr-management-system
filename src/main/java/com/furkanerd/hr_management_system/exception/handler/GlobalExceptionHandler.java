package com.furkanerd.hr_management_system.exception.handler;

import com.furkanerd.hr_management_system.exception.*;
import com.furkanerd.hr_management_system.model.dto.response.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUsernameNotFound(UsernameNotFoundException e, HttpServletRequest request) {
        log.error("User not found: {}", e.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("User Not Found")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(LeaveRequestAlreadyProcessedException.class)
    public ResponseEntity<ErrorResponseDto> handleAlreadyProcessedLeave(LeaveRequestAlreadyProcessedException e, HttpServletRequest request) {
        log.error("Leave request already processed: {}", e.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Leave Request Already Processed")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(LeaveRequestNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleLeaveRequestNotFound(LeaveRequestNotFoundException e, HttpServletRequest request) {
        log.error("Leave request not found: {}", e.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Leave Request Not Found")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InvalidAttendanceTimeException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidAttendanceTime(InvalidAttendanceTimeException e, HttpServletRequest request) {
        log.error("Invalid attendance time: {}", e.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Invalid Attendance Time")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AlreadyCheckedOutException.class)
    public ResponseEntity<ErrorResponseDto> handleAlreadyCheckedOut(AlreadyCheckedOutException e, HttpServletRequest request) {
        log.error("Already checked out: {}", e.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Already Checked Out")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AttendanceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleAttendanceNotFound(AttendanceNotFoundException e, HttpServletRequest request) {
        log.error("Attendance not found: {}", e.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Attendance Not Found")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(AttendanceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleAttendanceAlreadyExists(AttendanceAlreadyExistsException e, HttpServletRequest request) {
        log.error("Attendance already exists: {}", e.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Attendance Already Exists")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEmployeeNotFoundException(EmployeeNotFoundException e, HttpServletRequest request) {
        log.error("Employee not found: {}" , e.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Employee Not Found")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(PositionNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handlePositionNotFoundException(PositionNotFoundException e,HttpServletRequest request) {
        log.error("Position not found: {}" , e.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Position Not Found")
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DepartmentNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleDepartmentNotFoundException(DepartmentNotFoundException e,HttpServletRequest request) {
        log.error("Department not found: {}" , e.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Department Not Found")
                .message(e.getMessage())
                .path(request.getRequestURI())
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
