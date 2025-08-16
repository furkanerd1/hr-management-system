package com.furkanerd.hr_management_system.model.dto.response;

import com.furkanerd.hr_management_system.model.enums.EmployeeRoleEnum;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record LoginResponse(
         String token,
         UUID employeeId,
         String email,
         String firstName,
         String lastName,
         EmployeeRoleEnum role,
         List<String> roles,
         boolean mustChangePassword
){}
