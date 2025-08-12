package com.furkanerd.hr_management_system.model.dto.request.department;

import jakarta.validation.constraints.NotBlank;

public record DepartmentUpdateRequest(

        @NotBlank
        String name,
        String description
){}
