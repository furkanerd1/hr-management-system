package com.furkanerd.hr_management_system.exception.custom;

import com.furkanerd.hr_management_system.exception.base.HrValidationException;
import com.furkanerd.hr_management_system.model.enums.LeaveStatusEnum;

import java.util.UUID;

public class LeaveRequestAlreadyProcessedException extends HrValidationException {
    public LeaveRequestAlreadyProcessedException(UUID leaveRequestId, LeaveStatusEnum status) {
        super("LeaveRequest with ID " + leaveRequestId + " is already " + status);
    }

    @Override
    public String getErrorCode() {
        return "LEAVE_REQUEST_ALREADY_PROCESSED";
    }
}
