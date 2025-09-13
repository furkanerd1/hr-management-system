package com.furkanerd.hr_management_system.exception.handler;

import com.furkanerd.hr_management_system.model.dto.response.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class ExceptionResponseBuilder {

    public ErrorResponseDto buildErrorResponse(HttpStatus status, String errorCode, String message, String path) {
        return ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(errorCode)
                .message(message)
                .path(path)
                .build();
    }

    public ErrorResponseDto buildValidationErrorResponse(HttpStatus status, String errorCode, String message, String path, Map<String, String> validationErrors) {
        return ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(errorCode)
                .message(message)
                .path(path)
                .validationErrors(validationErrors)
                .build();
    }
}
