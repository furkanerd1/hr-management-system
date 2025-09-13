package com.furkanerd.hr_management_system.service.performancereview;

import com.furkanerd.hr_management_system.model.dto.request.performancereview.PerformanceReviewFilterRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.performancereview.ListPerformanceReviewResponse;
import com.furkanerd.hr_management_system.model.dto.response.performancereview.PerformanceReviewDetailResponse;

import java.util.UUID;

public interface PerformanceReviewQueryService {

    PaginatedResponse<ListPerformanceReviewResponse> listAllPerformanceReviews(int page, int size, String sortBy, String sortDirection, PerformanceReviewFilterRequest filterRequest);

    PerformanceReviewDetailResponse getPerformanceReview(UUID id);

    PaginatedResponse<ListPerformanceReviewResponse> getMyPerformanceReviews(String email, int page, int size, String sortBy, String sortDirection, PerformanceReviewFilterRequest filterRequest);

    PaginatedResponse<ListPerformanceReviewResponse> getPerformanceReviewsByEmployee(UUID employeeId, int page, int size, String sortBy, String sortDirection, PerformanceReviewFilterRequest filterRequest);

}
