package com.furkanerd.hr_management_system.model.dto.response.leaverequest;

import com.furkanerd.hr_management_system.model.enums.LeaveStatusEnum;
import com.furkanerd.hr_management_system.model.enums.LeaveTypeEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record LeaveRequestDetailResponse(
        UUID id,
        UUID employeeId,
        String employeeFullName,
        String email,
        String departmentName,
        String positionName,
        LeaveTypeEnum leaveType,
        LocalDate startDate,
        LocalDate endDate,
        Integer totalDays,
        String reason,
        LeaveStatusEnum status,
        String approverName,
        LocalDateTime approvedAt,
        LocalDateTime createdAt
){}
