package com.furkanerd.hr_management_system.model.dto.response.department;

import java.time.LocalDateTime;
import java.util.UUID;

public record DepartmentDetailResponse(
        UUID id,
        String name,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
