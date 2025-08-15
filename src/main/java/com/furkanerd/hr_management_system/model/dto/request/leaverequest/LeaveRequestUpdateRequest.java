package com.furkanerd.hr_management_system.model.dto.request.leaverequest;

import com.furkanerd.hr_management_system.model.enums.LeaveStatusEnum;
import jakarta.validation.constraints.NotNull;

public record LeaveRequestUpdateRequest(
        @NotNull
        LeaveStatusEnum status
){}
