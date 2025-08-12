package com.furkanerd.hr_management_system.model.dto.response.department;

import java.util.UUID;

public record ListDepartmentResponse(
        UUID id,
        String name
){}
