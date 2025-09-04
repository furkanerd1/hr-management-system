package com.furkanerd.hr_management_system.model.dto.request.leaverequest;

import com.furkanerd.hr_management_system.model.enums.LeaveStatusEnum;
import com.furkanerd.hr_management_system.model.enums.LeaveTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record LeaveRequestFilterRequest(
        LeaveTypeEnum leaveType,
        LeaveStatusEnum status,
        LocalDate startDateAfter,
        LocalDate startDateBefore,
        LocalDate endDateAfter,
        LocalDate endDateBefore,
        String searchTerm
) {
    public static LeaveRequestFilterRequest empty() {
        return LeaveRequestFilterRequest.builder().build();
    }

    @Schema(hidden = true)
    public boolean isEmpty() {
        return leaveType == null &&
                status == null &&
                startDateAfter == null &&
                startDateBefore == null &&
                endDateAfter == null &&
                endDateBefore == null &&
                (searchTerm == null || searchTerm.isBlank());
    }
}
