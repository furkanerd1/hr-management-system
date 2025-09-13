package com.furkanerd.hr_management_system.exception.custom;

import com.furkanerd.hr_management_system.exception.base.HrValidationException;

public class InvalidAttendanceTimeException extends HrValidationException {

    public InvalidAttendanceTimeException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return "INVALID_ATTENDANCE_TIME";
    }
}
