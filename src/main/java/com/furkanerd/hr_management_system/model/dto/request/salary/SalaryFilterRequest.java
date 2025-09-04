package com.furkanerd.hr_management_system.model.dto.request.salary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record SalaryFilterRequest(
        BigDecimal minSalary,
        BigDecimal maxSalary,
        BigDecimal minBonus,
        BigDecimal maxBonus,
        LocalDate effectiveDateAfter,
        LocalDate effectiveDateBefore,
        String searchTerm
){
    public static SalaryFilterRequest empty() {
        return SalaryFilterRequest.builder().build();
    }

    @Schema(hidden = true)
    public boolean isEmpty() {
        return minSalary == null && maxSalary == null &&
                minBonus == null && maxBonus == null &&
                effectiveDateAfter == null && effectiveDateBefore == null &&
                (searchTerm == null || searchTerm.isBlank());
    }
}
