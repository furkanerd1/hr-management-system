package com.furkanerd.hr_management_system.model.dto.request;

import com.furkanerd.hr_management_system.model.enums.EmployeeRoleEnum;
import com.furkanerd.hr_management_system.model.enums.EmployeeStatusEnum;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RegisterRequest(

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    String lastName,

@   NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    String email,

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number should be valid")
    String phone,

    @NotNull(message = "Hire date is required")
    @PastOrPresent(message = "Hire date cannot be in the future")
    LocalDate hireDate,

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    LocalDate birthDate,

    @Size(max = 1000, message = "Address must not exceed 1000 characters")
    String address,

    @NotNull(message = "Role is required")
    EmployeeRoleEnum role,

    @NotNull(message = "Status is required")
    EmployeeStatusEnum status,

    @NotNull(message = "Need a department information")
    UUID departmentId,

    @NotNull(message = "Need a position information")
    UUID positionId,

    UUID managerId
){}
