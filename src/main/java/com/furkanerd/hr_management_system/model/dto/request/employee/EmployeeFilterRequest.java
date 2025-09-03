package com.furkanerd.hr_management_system.model.dto.request.employee;

import com.furkanerd.hr_management_system.model.enums.EmployeeRoleEnum;
import com.furkanerd.hr_management_system.model.enums.EmployeeStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record EmployeeFilterRequest(
        String firstName,
        String lastName,
        String email,
        UUID departmentId,
        UUID positionId,
        EmployeeStatusEnum status,
        EmployeeRoleEnum role,
        String searchTerm,     // for global search
        LocalDate hireDateAfter,
        LocalDate hireDateBefore
){

    public static EmployeeFilterRequest empty() {
        return EmployeeFilterRequest.builder().build();
    }

    @Schema(hidden = true)
    public boolean isEmpty() {
        return firstName == null && lastName == null && email == null &&
                departmentId == null && positionId == null && status == null &&
                role == null && searchTerm == null && hireDateAfter == null &&
                hireDateBefore == null;
    }
}