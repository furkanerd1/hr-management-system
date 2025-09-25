package com.furkanerd.hr_management_system.model.dto.response.department;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ListDepartmentResponse(
        UUID id,
        String name
){}
