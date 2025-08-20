package com.furkanerd.hr_management_system.model.dto.response.employee;

import com.furkanerd.hr_management_system.model.enums.EmployeeRoleEnum;
import com.furkanerd.hr_management_system.model.enums.EmployeeStatusEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record EmployeeDetailResponse(
        UUID id,
        String fullName,
        String email,
        String phone,
        LocalDate hireDate,
        LocalDate birthDate,
        String address,
        String departmentName,
        String positionTitle,
        String managerFullName,
        EmployeeRoleEnum role,
        EmployeeStatusEnum status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){}
