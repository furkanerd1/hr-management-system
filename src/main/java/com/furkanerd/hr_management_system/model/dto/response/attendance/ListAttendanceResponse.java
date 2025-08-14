package com.furkanerd.hr_management_system.model.dto.response.attendance;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ListAttendanceResponse(
        UUID id,
        UUID employeeId,
        String employeeFullName,
        LocalDate date,
        LocalTime checkInTime,
        LocalTime checkOutTime
){}
