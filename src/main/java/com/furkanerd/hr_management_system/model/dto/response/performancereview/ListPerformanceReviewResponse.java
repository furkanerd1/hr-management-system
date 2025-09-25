package com.furkanerd.hr_management_system.model.dto.response.performancereview;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record ListPerformanceReviewResponse(
        UUID id,
        UUID employeeId,
        String employeeFullName,
        UUID reviewerId,
        String reviewerFullName,
        Integer rating,
        LocalDate reviewDate
){}
