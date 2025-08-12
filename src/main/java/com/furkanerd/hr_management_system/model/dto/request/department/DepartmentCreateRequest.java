package com.furkanerd.hr_management_system.model.dto.request.department;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DepartmentCreateRequest(

        @NotNull
        @NotEmpty
        @Size(max = 100)
        String name,

        String description
){}