package com.furkanerd.hr_management_system.model.dto.request.department;

import jakarta.validation.constraints.NotBlank;

public record DepartmentUpdateRequest(

        @NotBlank(message = "Department Name cannot be blank")
        String name,
        String description
){}
