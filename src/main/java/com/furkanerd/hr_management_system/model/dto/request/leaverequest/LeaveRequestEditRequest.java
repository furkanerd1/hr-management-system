package com.furkanerd.hr_management_system.model.dto.request.leaverequest;

import com.furkanerd.hr_management_system.model.enums.LeaveTypeEnum;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record LeaveRequestEditRequest(
        @NotNull
        LocalDate startDate,

        LocalDate endDate,

        @NotNull
        LeaveTypeEnum leaveType,

        String reason
){}
