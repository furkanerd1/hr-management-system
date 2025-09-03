package com.furkanerd.hr_management_system.specification;

import com.furkanerd.hr_management_system.model.dto.request.employee.EmployeeFilterRequest;
import com.furkanerd.hr_management_system.model.entity.Employee;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class EmployeeSpecification {

    private EmployeeSpecification() {

    }

    public static Specification<Employee> withFilters(EmployeeFilterRequest filterRequest) {

        if (filterRequest == null || filterRequest.isEmpty()) {
            return null;
        }

        return (root, query, criteriaBuilder) -> {
            // Each part of the WHERE condition (such as first_name LIKE '%john%')
            List<Predicate> predicates = new ArrayList<>();

            // root = SELECT * FROM employees(main table)
            // query = Full SQL query
            // criteriaBuilder = WHERE, AND, OR, LIKE - SQL ops tools

            // != null , !isEmpty()
            if (StringUtils.hasText(filterRequest.searchTerm())) {
                String searchPattern = "%" + filterRequest.searchTerm().toLowerCase() + "%";
                Predicate globalSearch = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), searchPattern)
                );
                predicates.add(globalSearch);
            }

            // First name - partial match
            if (StringUtils.hasText(filterRequest.firstName())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("firstName")),
                        "%" + filterRequest.firstName().toLowerCase() + "%"
                ));
            }

            // Last name - partial match
            if (StringUtils.hasText(filterRequest.lastName())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("lastName")),
                        "%" + filterRequest.lastName().toLowerCase() + "%"
                ));
            }

            // Email - partial match
            if (StringUtils.hasText(filterRequest.email())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")),
                        "%" + filterRequest.email().toLowerCase() + "%"
                ));
            }

            // Department - exact match
            if (filterRequest.departmentId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("department").get("id"),
                        filterRequest.departmentId()
                ));
            }

            // Position - exact match
            if (filterRequest.positionId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("position").get("id"),
                        filterRequest.positionId()
                ));
            }

            // Status - exact match
            if (filterRequest.status() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("status"),
                        filterRequest.status()
                ));
            }

            // Role - exact match
            if (filterRequest.role() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("role"),
                        filterRequest.role()
                ));
            }

            // Hire date range
            if (filterRequest.hireDateAfter() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("hireDate"),
                        filterRequest.hireDateAfter()
                ));
            }

            if (filterRequest.hireDateBefore() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("hireDate"),
                        filterRequest.hireDateBefore()
                ));
            }

            // Combine all predicates with AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
