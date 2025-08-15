package com.furkanerd.hr_management_system.model.dto.request.leaverequest;

import com.furkanerd.hr_management_system.model.enums.LeaveTypeEnum;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record LeaveRequestCreateRequest(

        @NotNull(message = "Leave type cannot be null")
        LeaveTypeEnum leaveType,

        @NotNull(message = "Start date cannot be null")
        @FutureOrPresent(message = "Start date must be in the present or future")
        LocalDate startDate,

        @NotNull(message = "End date cannot be null")
        @FutureOrPresent(message = "End date must be in the present or future")
        LocalDate endDate,

        String reason
){}
