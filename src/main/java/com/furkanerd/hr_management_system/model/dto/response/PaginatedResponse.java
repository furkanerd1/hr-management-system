package com.furkanerd.hr_management_system.model.dto.response;

import java.util.List;

public record PaginatedResponse<T>(
        List<T> data,
        long total,
        int page,           // Current page (0-based)
        int size,           // Page size
        int totalPages,     // Total number of pages
        boolean hasNext,    // Has next page
        boolean hasPrevious
) {
    public static <T> PaginatedResponse<T> of(List<T> data, long total, int page, int size) {
        int totalPages = (int) Math.ceil((double) total / size);
        boolean hasNext = page < totalPages - 1;
        boolean hasPrevious = page > 0;

        return new PaginatedResponse<>(
                data,
                total,
                page,
                size,
                totalPages,
                hasNext,
                hasPrevious
        );
    }
}


