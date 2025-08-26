package com.furkanerd.hr_management_system.model.dto.request.performancereview;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PerformanceReviewUpdateRequest(

        @NotNull(message = "Rating cannot be null")
        @Min(value = 1, message = "Rating cannot be lower than 1")
        @Max(value = 5, message = "Rating cannot be higher than 5")
        Integer rating,

        @NotBlank(message = "Comments cannot be blank")
        @Size(max=1000)
        String comments,

        LocalDate reviewDate
){}
