package com.furkanerd.hr_management_system.util;

import java.util.List;
import java.util.Map;

public class SortFieldValidator {

    private static final Map<String, List<String>> VALID_FIELDS = Map.of(
            "employee", List.of("id", "firstName", "lastName", "email", "phone", "hireDate", "birthDate", "address", "status", "role", "createdAt", "updatedAt"),
            "department", List.of("id", "name", "createdAt", "updatedAt"),
            "leaveRequest", List.of("id", "startDate", "endDate", "totalDays", "status", "leaveType", "createdAt", "updatedAt"),
            "position", List.of("id", "title", "createdAt", "updatedAt"),
            "attendance", List.of("id", "date", "checkInTime", "checkOutTime", "createdAt", "updatedAt"),
            "performanceReview", List.of("id", "rating", "reviewDate", "createdAt", "updatedAt"),
            "salary",List.of("id","salary","bonus","effectiveDate","createdAt", "updatedAt")
    );

    private static final Map<String, String> DEFAULT_FIELDS = Map.of(
            "employee", "firstName",
            "department", "name",
            "leaveRequest", "startDate",
            "position", "title",
            "attendance", "date",
            "performanceReview", "reviewDate",
            "salary","effectiveDate"
    );

    private SortFieldValidator() {
    }

    /**
     * Validate sortBy field for a given domain.
     * Falls back to domain default if input is invalid.
     */
    public static String validate(String domain, String sortBy) {
        List<String> allowedFields = VALID_FIELDS.getOrDefault(domain, List.of());
        String defaultField = DEFAULT_FIELDS.getOrDefault(domain, "id");

        return allowedFields.contains(sortBy) ? sortBy : defaultField;
    }
}
