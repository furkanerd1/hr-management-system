package com.furkanerd.hr_management_system.service;

import com.furkanerd.hr_management_system.model.dto.request.performancereview.PerformanceReviewCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.performancereview.PerformanceReviewUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.performancereview.ListPerformanceReviewResponse;
import com.furkanerd.hr_management_system.model.dto.response.performancereview.PerformanceReviewDetailResponse;

import java.util.List;
import java.util.UUID;

public interface PerformanceReviewService {

   PaginatedResponse<ListPerformanceReviewResponse> listAllPerformanceReviews(int page,int size,String sortBy,String sortDirection);

    PerformanceReviewDetailResponse getPerformanceReview(UUID id);

    PaginatedResponse<ListPerformanceReviewResponse> getMyPerformanceReviews(String email,int page,int size,String sortBy,String sortDirection);

    PerformanceReviewDetailResponse createPerformanceReview(PerformanceReviewCreateRequest performanceReviewCreateRequest, String email);

    PerformanceReviewDetailResponse updatePerformanceReview(UUID id, PerformanceReviewUpdateRequest updateRequest, String reviewerEmail);

    void deletePerformanceReview(UUID id, String reviewerEmail);

    PaginatedResponse<ListPerformanceReviewResponse> getPerformanceReviewByEmployeeId(UUID employeeId,int page, int size, String sortBy, String sortDirection);
}
