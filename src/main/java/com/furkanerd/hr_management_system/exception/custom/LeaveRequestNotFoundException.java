package com.furkanerd.hr_management_system.exception.custom;

import com.furkanerd.hr_management_system.exception.base.HrNotFoundException;

import java.util.UUID;

public class LeaveRequestNotFoundException extends HrNotFoundException {

    public LeaveRequestNotFoundException(UUID leaveRequestId) {
        super("LeaveRequest", leaveRequestId);
    }

    @Override
    public String getErrorCode() {
        return "LEAVE_REQUEST_NOT_FOUND";
    }
}

