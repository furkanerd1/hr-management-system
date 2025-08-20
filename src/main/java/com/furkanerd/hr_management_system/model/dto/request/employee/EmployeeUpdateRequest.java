package com.furkanerd.hr_management_system.model.dto.request.employee;

import com.furkanerd.hr_management_system.model.enums.EmployeeStatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record EmployeeUpdateRequest(

        @NotBlank(message = "First name cannot be blank")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,

        @NotBlank(message = "Phone number cannot be blank")
        @Size(max = 20, message = "Phone number cannot exceed 20 characters")
        @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number must be valid")
        String phone,

        @Size(max = 1000, message = "Address cannot exceed 1000 characters")
        String address,

        @NotNull(message = "Department ID cannot be null")
        UUID departmentId,

        @NotNull(message = "Position ID cannot be null")
        UUID positionId,

        UUID managerId,

        EmployeeStatusEnum status
){}
