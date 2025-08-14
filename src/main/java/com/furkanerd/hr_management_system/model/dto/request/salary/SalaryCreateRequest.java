package com.furkanerd.hr_management_system.model.dto.request.salary;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record SalaryCreateRequest(

        @NotNull(message = "Employee ID is required")
        UUID employeeId,

        @NotNull(message = "Base salary is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Base salary must be greater than 0")
        @Digits(integer = 8, fraction = 2, message = "Invalid salary format")
        BigDecimal salary,

        @DecimalMin(value = "0.0", message = "Bonus cannot be negative")
        @Digits(integer = 8, fraction = 2, message = "Invalid bonus format")
        BigDecimal bonus,

        @NotNull(message = "Effective date is required")
        @FutureOrPresent(message = "Effective date cannot be in the past")
        LocalDate effectiveDate

){}
