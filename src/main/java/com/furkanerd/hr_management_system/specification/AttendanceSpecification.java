package com.furkanerd.hr_management_system.specification;

import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceFilterRequest;
import com.furkanerd.hr_management_system.model.entity.Attendance;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AttendanceSpecification {

    private AttendanceSpecification() {}

    public static Specification<Attendance> withFilters(AttendanceFilterRequest filter) {
        if (filter == null || filter.isEmpty()) {
            return null;
        }

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // dateAfter
            if (filter.dateAfter() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), filter.dateAfter()));
            }

            // dateBefore
            if (filter.dateBefore() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), filter.dateBefore()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
