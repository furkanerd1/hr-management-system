package com.furkanerd.hr_management_system.model.dto.response.position;

import java.time.LocalDateTime;
import java.util.UUID;

public record PositionDetailResponse(
        UUID id,
        String title,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

){}
