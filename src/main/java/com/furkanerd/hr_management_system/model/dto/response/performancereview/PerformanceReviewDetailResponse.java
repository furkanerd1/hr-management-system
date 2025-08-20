package com.furkanerd.hr_management_system.model.dto.response.performancereview;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PerformanceReviewDetailResponse(
        UUID id,
        UUID employeeId,
        String employeeFullName,
        String email,
        String departmentName,
        String positionName,
        String managerFullName,
        String reviewerFullName,
        Integer rating,
        String comments,
        LocalDate reviewDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){}
