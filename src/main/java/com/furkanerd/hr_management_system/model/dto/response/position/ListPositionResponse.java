package com.furkanerd.hr_management_system.model.dto.response.position;

import java.util.UUID;

public record ListPositionResponse(
        UUID id,
        String title
){}
