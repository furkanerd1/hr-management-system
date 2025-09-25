package com.furkanerd.hr_management_system.model.dto.response.salary;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record ListSalaryResponse(
        UUID id,
        UUID employeeId,
        String employeeFullName,
        BigDecimal salary,
        BigDecimal bonus,
        BigDecimal totalSalary,
        LocalDate effectiveDate
){}
