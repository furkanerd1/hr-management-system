package com.furkanerd.hr_management_system.model.dto.response.employee;

import com.furkanerd.hr_management_system.model.enums.EmployeeStatusEnum;

import java.util.UUID;

public record ListEmployeeResponse(
        UUID id,
        String fullName,
        String email,
        String phone,
        String departmentName,
        String positionTitle,
        EmployeeStatusEnum status
){}
