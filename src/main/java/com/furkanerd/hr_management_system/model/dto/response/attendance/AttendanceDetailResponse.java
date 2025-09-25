package com.furkanerd.hr_management_system.model.dto.response.attendance;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Builder
public record AttendanceDetailResponse(
        UUID id,
        UUID employeeId,
        String employeeFullName,
        String email,
        String departmentName,
        String positionName,
        LocalDate date,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){}
