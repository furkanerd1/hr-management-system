package com.furkanerd.hr_management_system.controller;

import com.furkanerd.hr_management_system.model.dto.request.performancereview.PerformanceReviewCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.performancereview.PerformanceReviewUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.ApiResponse;
import com.furkanerd.hr_management_system.model.dto.response.performancereview.ListPerformanceReviewResponse;
import com.furkanerd.hr_management_system.model.dto.response.performancereview.PerformanceReviewDetailResponse;
import com.furkanerd.hr_management_system.service.PerformanceReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.furkanerd.hr_management_system.config.ApiPaths.PERFORMANCE_REVIEWS;

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
    // TODO: Convert to PaginatedResponse when pagination is implemented
    public ResponseEntity<ApiResponse<List<ListPerformanceReviewResponse>>> getAllReviews () {
        List<ListPerformanceReviewResponse> reviews = performanceReviewService.listAllPerformanceReviews();
        return ResponseEntity.ok(ApiResponse.success("Performance reviews retrieved successfully", reviews));
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
    // TODO: Convert to PaginatedResponse when pagination is implemented
    public ResponseEntity<ApiResponse<List<ListPerformanceReviewResponse>>> getMyReviews(@AuthenticationPrincipal UserDetails currentUser) {
        List<ListPerformanceReviewResponse> reviews = performanceReviewService.getMyPerformanceReviews(currentUser.getUsername());
        return ResponseEntity.ok(ApiResponse.success("My performance reviews retrieved successfully", reviews));
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
            ){
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
        PerformanceReviewDetailResponse updated =  performanceReviewService.updatePerformanceReview(id, updateRequest, reviewerEmail);
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
