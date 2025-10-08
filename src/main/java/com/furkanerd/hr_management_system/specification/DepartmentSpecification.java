package com.furkanerd.hr_management_system.specification;

import com.furkanerd.hr_management_system.model.dto.request.department.DepartmentFilterRequest;
import com.furkanerd.hr_management_system.model.entity.Department;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DepartmentSpecification {

    private DepartmentSpecification() {}

    public static Specification<Department> withFilters(DepartmentFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter != null && !filter.isEmpty()) {
                if (StringUtils.hasText(filter.name())) {
                    predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.name().toLowerCase() + "%"));
                }

                if (StringUtils.hasText(filter.description())) {
                    predicates.add(cb.like(cb.lower(root.get("description")), "%" + filter.description().toLowerCase() + "%"));
                }

                if (StringUtils.hasText(filter.searchTerm())) {
                    String pattern = "%" + filter.searchTerm().toLowerCase() + "%";
                    predicates.add(cb.or(
                            cb.like(cb.lower(root.get("name")), pattern),
                            cb.like(cb.lower(root.get("description")), pattern)
                    ));
                }
            }

            return predicates.isEmpty() ?
                    cb.conjunction() :
                    cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
