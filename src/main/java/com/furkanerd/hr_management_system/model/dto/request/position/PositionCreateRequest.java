package com.furkanerd.hr_management_system.model.dto.request.position;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PositionCreateRequest(

        @NotNull
        @NotEmpty(message = "Please enter the position title !")
        @Size(max = 100)
        String title,

        String description
){}
