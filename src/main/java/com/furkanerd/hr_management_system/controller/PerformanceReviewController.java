package com.furkanerd.hr_management_system.controller;

import com.furkanerd.hr_management_system.model.dto.request.performancereview.PerformanceReviewCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.performancereview.PerformanceReviewFilterRequest;
import com.furkanerd.hr_management_system.model.dto.request.performancereview.PerformanceReviewUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.ApiResponse;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.performancereview.ListPerformanceReviewResponse;
import com.furkanerd.hr_management_system.model.dto.response.performancereview.PerformanceReviewDetailResponse;
import com.furkanerd.hr_management_system.service.PerformanceReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

import static com.furkanerd.hr_management_system.constants.ApiPaths.PERFORMANCE_REVIEWS;

@RestController
@RequestMapping(PERFORMANCE_REVIEWS)
@Tag(name = "Performance Review", description = "Employee Performance Review management API")
public class PerformanceReviewController {

    private final PerformanceReviewService performanceReviewService;

    public PerformanceReviewController(PerformanceReviewService performanceReviewService) {
        this.performanceReviewService = performanceReviewService;
    }

    @Operation(
            summary = "Get all performance reviews",
            description = "Retrieves a list of all performance review records. Restricted to HR and Manager roles."
    )
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<PaginatedResponse<ListPerformanceReviewResponse>>> getAllReviews(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "reviewDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            PerformanceReviewFilterRequest filterRequest
    ) {
        PaginatedResponse<ListPerformanceReviewResponse> responseList = performanceReviewService.listAllPerformanceReviews(page, size, sortBy, sortDirection, filterRequest);
        return ResponseEntity.ok(ApiResponse.success("Performance reviews retrieved successfully", responseList));
    }

    @Operation(
            summary = "Get a performance review by ID",
            description = "Retrieves a specific performance review record using its unique ID. Restricted to HR and Manager roles."
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<PerformanceReviewDetailResponse>> getReview(@PathVariable("id") UUID id) {
        PerformanceReviewDetailResponse review = performanceReviewService.getPerformanceReview(id);
        return ResponseEntity.ok(ApiResponse.success("Performance review retrieved successfully", review));
    }

    @Operation(
            summary = "Get authenticated user's performance reviews",
            description = "Retrieves a list of all performance review records for the authenticated user. Accessible to all employees.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/my-reviews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaginatedResponse<ListPerformanceReviewResponse>>> getMyReviews(
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "reviewDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            PerformanceReviewFilterRequest filterRequest
    ) {
        PaginatedResponse<ListPerformanceReviewResponse> responseList = performanceReviewService.getMyPerformanceReviews(currentUser.getUsername(), page, size, sortBy, sortDirection, filterRequest);
        return ResponseEntity.ok(ApiResponse.success("My performance reviews retrieved successfully", responseList));
    }


    @Operation(
            summary = "Get performance history for a specific employee",
            description = "Retrieves a list of performance reviews for a specified employee by ID. This action is restricted to users with the HR or Manager role.")
    @GetMapping("/employee/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<PaginatedResponse<ListPerformanceReviewResponse>>> getEmployeePerformanceHistory(
            @PathVariable("id") UUID id,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "reviewDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            PerformanceReviewFilterRequest filterRequest
    ) {
        PaginatedResponse<ListPerformanceReviewResponse> responseList =
                performanceReviewService.getPerformanceReviewsByEmployee(id, page, size, sortBy, sortDirection, filterRequest);
        return ResponseEntity.ok(ApiResponse.success("Performance reviews retrieved successfully", responseList));
    }


    @Operation(
            summary = "Create a new performance review",
            description = "Creates a new performance review record for an employee. Restricted to HR and Manager roles."
    )
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<PerformanceReviewDetailResponse>> createReview(
            @Valid @RequestBody PerformanceReviewCreateRequest createRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        PerformanceReviewDetailResponse created = performanceReviewService.createPerformanceReview(createRequest, email);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Performance review created successfully", created));
    }

    @Operation(
            summary = "Update a performance review by ID",
            description = "Updates an existing performance review record. Restricted to HR and Manager roles.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<PerformanceReviewDetailResponse>> updateReview(
            @PathVariable UUID id,
            @Valid @RequestBody PerformanceReviewUpdateRequest updateRequest,
            @AuthenticationPrincipal UserDetails currentUser) {
        String reviewerEmail = currentUser.getUsername();
        PerformanceReviewDetailResponse updated = performanceReviewService.updatePerformanceReview(id, updateRequest, reviewerEmail);
        return ResponseEntity.ok(ApiResponse.success("Performance review updated successfully", updated));
    }


    @Operation(
            summary = "Delete a performance review by ID", description = "Deletes a performance review record by its unique ID. Restricted to HR and Manager roles.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable UUID id, @AuthenticationPrincipal UserDetails currentUser) {
        String reviewerEmail = currentUser.getUsername();
        performanceReviewService.deletePerformanceReview(id, reviewerEmail);
        return ResponseEntity.ok(ApiResponse.success("Performance review deleted successfully"));
    }
}
