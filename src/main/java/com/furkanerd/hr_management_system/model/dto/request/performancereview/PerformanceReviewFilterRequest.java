package com.furkanerd.hr_management_system.model.dto.request.performancereview;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record PerformanceReviewFilterRequest(
        UUID reviewerId,
        Integer minRating,
        Integer maxRating,
        LocalDate reviewDateAfter,
        LocalDate reviewDateBefore,
        String searchTerm
) {
    public static PerformanceReviewFilterRequest empty() {
        return PerformanceReviewFilterRequest.builder().build();
    }

    @Schema(hidden = true)
    public boolean isEmpty() {
        return reviewerId == null &&
                minRating == null &&
                maxRating == null &&
                reviewDateAfter == null &&
                reviewDateBefore == null &&
                (searchTerm == null || searchTerm.isBlank());
    }
}
