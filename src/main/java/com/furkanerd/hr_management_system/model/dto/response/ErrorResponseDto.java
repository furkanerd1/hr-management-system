package com.furkanerd.hr_management_system.model.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
public record ErrorResponseDto(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path,
    Map<String,String> validationErrors
){}
