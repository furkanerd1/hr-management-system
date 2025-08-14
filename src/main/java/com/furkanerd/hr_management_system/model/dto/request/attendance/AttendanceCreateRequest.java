package com.furkanerd.hr_management_system.model.dto.request.attendance;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AttendanceCreateRequest(

        @NotNull(message = "Employee ID cannot be null")
        UUID employeeId,

        @NotNull(message = "Date cannot be null")
        LocalDate date,

        @NotNull(message = "Check-in time cannot be null")
        LocalTime checkInTime,

        LocalTime checkOutTime
){}
