package com.furkanerd.hr_management_system.specification;

import com.furkanerd.hr_management_system.model.dto.request.performancereview.PerformanceReviewFilterRequest;
import com.furkanerd.hr_management_system.model.entity.PerformanceReview;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PerformanceReviewSpecification {
    private PerformanceReviewSpecification() {}

    public static Specification<PerformanceReview> withFilters(PerformanceReviewFilterRequest filter) {
        if (filter == null || filter.isEmpty()) {
            return null;
        }

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.reviewerId() != null) {
                predicates.add(cb.equal(root.get("reviewer").get("id"), filter.reviewerId()));
            }

            if (filter.minRating() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), filter.minRating()));
            }

            if (filter.maxRating() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("rating"), filter.maxRating()));
            }

            if (filter.reviewDateAfter() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("reviewDate"), filter.reviewDateAfter()));
            }

            if (filter.reviewDateBefore() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("reviewDate"), filter.reviewDateBefore()));
            }

            if (StringUtils.hasText(filter.searchTerm())) {
                String pattern = "%" + filter.searchTerm().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("comments")), pattern));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
