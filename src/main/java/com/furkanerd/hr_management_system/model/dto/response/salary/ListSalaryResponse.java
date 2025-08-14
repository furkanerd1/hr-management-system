package com.furkanerd.hr_management_system.model.dto.response.salary;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ListSalaryResponse(
        UUID id,
        String employeeFullName,
        BigDecimal salary,
        BigDecimal bonus,
        BigDecimal totalSalary,
        LocalDate effectiveDate
){}
