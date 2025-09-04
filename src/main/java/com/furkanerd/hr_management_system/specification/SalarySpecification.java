package com.furkanerd.hr_management_system.specification;

import com.furkanerd.hr_management_system.model.dto.request.salary.SalaryFilterRequest;
import com.furkanerd.hr_management_system.model.entity.Salary;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class SalarySpecification {

    private SalarySpecification() {}

    public static Specification<Salary> withFilters(SalaryFilterRequest filter) {
        if (filter == null || filter.isEmpty()) {
            return null;
        }

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // minSalary
            if (filter.minSalary() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("salary"), filter.minSalary()));
            }

            // maxSalary
            if (filter.maxSalary() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("salary"), filter.maxSalary()));
            }

            // minBonus
            if (filter.minBonus() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("bonus"), filter.minBonus()));
            }

            // maxBonus
            if (filter.maxBonus() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("bonus"), filter.maxBonus()));
            }

            // effectiveDate range
            if (filter.effectiveDateAfter() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("effectiveDate"), filter.effectiveDateAfter()));
            }

            // effectiveDateBefore

            if (filter.effectiveDateBefore() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("effectiveDate"), filter.effectiveDateBefore()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
