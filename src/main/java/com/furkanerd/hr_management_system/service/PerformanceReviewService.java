package com.furkanerd.hr_management_system.service;

import com.furkanerd.hr_management_system.model.dto.request.performancereview.PerformanceReviewCreateRequest;
import com.furkanerd.hr_management_system.model.dto.response.performancereview.ListPerformanceReviewResponse;
import com.furkanerd.hr_management_system.model.dto.response.performancereview.PerformanceReviewDetailResponse;

import java.util.List;
import java.util.UUID;

public interface PerformanceReviewService {

    List<ListPerformanceReviewResponse> listAllPerformanceReviews();

    PerformanceReviewDetailResponse getPerformanceReview(UUID id);

    PerformanceReviewDetailResponse createPerformanceReview(PerformanceReviewCreateRequest performanceReviewCreateRequest, String email);
}
