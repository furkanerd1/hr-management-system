package com.furkanerd.hr_management_system.model.dto.request.position;

import jakarta.validation.constraints.NotBlank;

public record PositionUpdateRequest(

        @NotBlank(message = "Position Title cannot be blank")
        String title,
        String description
){}
