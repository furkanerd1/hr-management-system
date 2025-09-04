package com.furkanerd.hr_management_system.model.dto.request.position;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record PositionFilterRequest(
        String title,
        String description,
        String searchTerm
) {
    public static PositionFilterRequest empty() {
        return PositionFilterRequest.builder().build();
    }

    @Schema(hidden = true)
    public boolean isEmpty() {
        return (title == null || title.isBlank()) &&
                (description == null || description.isBlank()) &&
                (searchTerm == null || searchTerm.isBlank());
    }
}
