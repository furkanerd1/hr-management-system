package com.furkanerd.hr_management_system.model.dto.response.salary;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record SalaryDetailResponse(
        UUID id,
        UUID employeeId,
        String employeeFullName,
        String phone,
        String email,
        String departmentName,
        String positionName,
        BigDecimal salary,
        BigDecimal bonus,
        BigDecimal totalSalary,
        LocalDate effectiveDate,
        LocalDateTime createdAt
){}
