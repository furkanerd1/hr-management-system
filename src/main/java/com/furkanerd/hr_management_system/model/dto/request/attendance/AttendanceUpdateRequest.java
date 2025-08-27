package com.furkanerd.hr_management_system.model.dto.request.attendance;

import java.time.LocalTime;

public record AttendanceUpdateRequest(
        LocalTime checkInTime,
        LocalTime checkOutTime
){}
