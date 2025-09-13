package com.furkanerd.hr_management_system.exception.custom;

import com.furkanerd.hr_management_system.exception.base.HrValidationException;

public class InsufficientLeaveBalanceException extends HrValidationException {

    public InsufficientLeaveBalanceException(String leaveType, int available, int requested) {
        super(String.format("Insufficient %s balance. Available: %d, Requested: %d",
                leaveType.toLowerCase(), available, requested));
    }

    @Override
    public String getErrorCode() {
        return "INSUFFICIENT_LEAVE_BALANCE";
    }
}
