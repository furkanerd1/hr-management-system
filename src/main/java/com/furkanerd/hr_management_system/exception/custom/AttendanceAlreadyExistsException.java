package com.furkanerd.hr_management_system.exception.custom;

import com.furkanerd.hr_management_system.exception.base.HrConflictException;

import java.time.LocalDate;
import java.util.UUID;

public class AttendanceAlreadyExistsException extends HrConflictException {

    public AttendanceAlreadyExistsException(UUID employeeId, LocalDate date) {
        super(String.format("Attendance already exists for employee: %s on date: %s", employeeId, date));
    }

    @Override
    public String getErrorCode() {
        return "ATTENDANCE_ALREADY_EXISTS";
    }
}
