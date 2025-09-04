package com.furkanerd.hr_management_system.model.dto.request.attendance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AttendanceFilterRequest(
        LocalDate dateAfter,
        LocalDate dateBefore
) {
    public static AttendanceFilterRequest empty() {
        return AttendanceFilterRequest.builder().build();
    }

    @Schema(hidden = true)
    public boolean isEmpty() {
        return dateAfter == null && dateBefore == null;
    }
}
