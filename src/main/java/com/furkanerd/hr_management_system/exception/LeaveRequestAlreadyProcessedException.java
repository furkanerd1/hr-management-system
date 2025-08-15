package com.furkanerd.hr_management_system.exception;

import com.furkanerd.hr_management_system.model.enums.LeaveStatusEnum;

import java.util.UUID;

public class LeaveRequestAlreadyProcessedException extends RuntimeException {
    public LeaveRequestAlreadyProcessedException(UUID leaveRequestId, LeaveStatusEnum status) {
        super("LeaveRequest with ID " + leaveRequestId + " is already " + status);
    }
}
