package com.furkanerd.hr_management_system.service.performancereview;

import com.furkanerd.hr_management_system.model.dto.request.performancereview.PerformanceReviewCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.performancereview.PerformanceReviewUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.performancereview.PerformanceReviewDetailResponse;

import java.util.UUID;

public interface PerformanceReviewManagementService{
    PerformanceReviewDetailResponse createPerformanceReview(PerformanceReviewCreateRequest performanceReviewCreateRequest, String email);
    PerformanceReviewDetailResponse updatePerformanceReview(UUID id, PerformanceReviewUpdateRequest updateRequest, String reviewerEmail);
    void deletePerformanceReview(UUID id, String reviewerEmail);

}
