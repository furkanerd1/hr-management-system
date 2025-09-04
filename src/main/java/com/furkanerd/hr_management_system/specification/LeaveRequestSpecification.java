package com.furkanerd.hr_management_system.specification;

import com.furkanerd.hr_management_system.model.dto.request.leaverequest.LeaveRequestFilterRequest;
import com.furkanerd.hr_management_system.model.entity.LeaveRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class LeaveRequestSpecification {
    private LeaveRequestSpecification() {}

    public static Specification<LeaveRequest> withFilters(LeaveRequestFilterRequest filter) {

        if (filter == null || filter.isEmpty()) {
            return null;
        }

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.leaveType() != null) {
                predicates.add(cb.equal(root.get("leaveType"), filter.leaveType()));
            }

            if (filter.status() != null) {
                predicates.add(cb.equal(root.get("status"), filter.status()));
            }

            if (filter.startDateAfter() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), filter.startDateAfter()));
            }

            if (filter.startDateBefore() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startDate"), filter.startDateBefore()));
            }

            if (filter.endDateAfter() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("endDate"), filter.endDateAfter()));
            }

            if (filter.endDateBefore() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("endDate"), filter.endDateBefore()));
            }

            if (StringUtils.hasText(filter.searchTerm())) {
                String pattern = "%" + filter.searchTerm().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("reason")), pattern));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
