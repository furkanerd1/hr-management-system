package com.furkanerd.hr_management_system.specification;

import com.furkanerd.hr_management_system.model.dto.request.position.PositionFilterRequest;
import com.furkanerd.hr_management_system.model.entity.Position;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PositionSpecification {

    private PositionSpecification() {}

    public static Specification<Position> withFilters(PositionFilterRequest filter) {
        if (filter == null || filter.isEmpty()) {
            return null;
        }

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(filter.title())) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + filter.title().toLowerCase() + "%"));
            }

            if (StringUtils.hasText(filter.description())) {
                predicates.add(cb.like(cb.lower(root.get("description")), "%" + filter.description().toLowerCase() + "%"));
            }

            if (StringUtils.hasText(filter.searchTerm())) {
                String pattern = "%" + filter.searchTerm().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
