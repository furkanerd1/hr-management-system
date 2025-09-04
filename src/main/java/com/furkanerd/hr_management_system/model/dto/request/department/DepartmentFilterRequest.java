package com.furkanerd.hr_management_system.model.dto.request.department;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record DepartmentFilterRequest(
        String name,
        String description,
        String searchTerm
){
    public static DepartmentFilterRequest empty() {
        return DepartmentFilterRequest.builder().build();
    }

    @Schema(hidden = true)
    public boolean isEmpty() {
        return (name == null || name.isBlank()) &&
                (description == null || description.isBlank()) &&
                (searchTerm == null || searchTerm.isBlank());
    }
}
