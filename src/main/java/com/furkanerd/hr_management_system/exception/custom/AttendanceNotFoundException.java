package com.furkanerd.hr_management_system.exception.custom;

import com.furkanerd.hr_management_system.exception.base.HrNotFoundException;

import java.util.UUID;

public class AttendanceNotFoundException extends HrNotFoundException {

    public AttendanceNotFoundException(UUID attendanceId) {
        super("Attendance", attendanceId);
    }

    public AttendanceNotFoundException(String message) {
        super("Attendance", message);
    }

    @Override
    public String getErrorCode() {
        return "ATTENDANCE_NOT_FOUND";
    }
}
