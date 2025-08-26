package com.furkanerd.hr_management_system.controller;

import com.furkanerd.hr_management_system.model.dto.request.performancereview.PerformanceReviewCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.performancereview.PerformanceReviewUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.performancereview.ListPerformanceReviewResponse;
import com.furkanerd.hr_management_system.model.dto.response.performancereview.PerformanceReviewDetailResponse;
import com.furkanerd.hr_management_system.service.PerformanceReviewService;
import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseEntity<List<ListPerformanceReviewResponse>> getAllReviews () {
        return ResponseEntity.ok(performanceReviewService.listAllPerformanceReviews());
    }

    @Operation(
            summary = "Get a performance review by ID",
            description = "Retrieves a specific performance review record using its unique ID. Restricted to HR and Manager roles."
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<PerformanceReviewDetailResponse> getReview(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(performanceReviewService.getPerformanceReview(id));
    }

    @GetMapping("/my-reviews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ListPerformanceReviewResponse>> getMyReviews(@AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(performanceReviewService.getMyPerformanceReviews(currentUser.getUsername()));
    }


    @Operation(
            summary = "Create a new performance review",
            description = "Creates a new performance review record for an employee. Restricted to HR and Manager roles."
    )
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<PerformanceReviewDetailResponse> createReview(
            @Valid @RequestBody PerformanceReviewCreateRequest createRequest,
            @AuthenticationPrincipal UserDetails userDetails
            ){
        String email =  userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.CREATED).body(performanceReviewService.createPerformanceReview(createRequest,email));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<PerformanceReviewDetailResponse> updateReview(
            @PathVariable UUID id,
            @Valid @RequestBody PerformanceReviewUpdateRequest updateRequest,
            @AuthenticationPrincipal UserDetails currentUser) {
        String reviewerEmail = currentUser.getUsername();
        return ResponseEntity.ok(performanceReviewService.updatePerformanceReview(id, updateRequest, reviewerEmail));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<Void> deleteReview(@PathVariable UUID id,
                                             @AuthenticationPrincipal UserDetails currentUser) {
        String reviewerEmail = currentUser.getUsername();
        performanceReviewService.deletePerformanceReview(id, reviewerEmail);
        return ResponseEntity.noContent().build();
    }
}
