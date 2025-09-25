package com.furkanerd.hr_management_system.model.dto.response.department;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record DepartmentDetailResponse(
        UUID id,
        String name,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
