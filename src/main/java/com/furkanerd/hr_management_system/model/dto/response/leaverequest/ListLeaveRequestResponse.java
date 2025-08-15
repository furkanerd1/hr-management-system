package com.furkanerd.hr_management_system.model.dto.response.leaverequest;

import com.furkanerd.hr_management_system.model.enums.LeaveStatusEnum;
import com.furkanerd.hr_management_system.model.enums.LeaveTypeEnum;

import java.time.LocalDate;
import java.util.UUID;

public record ListLeaveRequestResponse(
        UUID id,
        UUID employeeId,
        String employeeFullName,
        LeaveTypeEnum leaveType,
        LocalDate startDate,
        LocalDate endDate,
        Integer totalDays,
        LeaveStatusEnum status
){}
