package com.furkanerd.hr_management_system.model.dto.response;

import com.furkanerd.hr_management_system.model.enums.EmployeeRoleEnum;

import java.util.List;
import java.util.UUID;

public record RegisterResponse(
        UUID employeeId,
        String email,
        String firstName,
        String lastName,
        EmployeeRoleEnum role,
        String password,
        List<String> roles
){}
