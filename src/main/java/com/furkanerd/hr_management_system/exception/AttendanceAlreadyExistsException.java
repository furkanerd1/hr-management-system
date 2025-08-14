package com.furkanerd.hr_management_system.exception;

import java.time.LocalDate;
import java.util.UUID;

public class AttendanceAlreadyExistsException extends RuntimeException {
    public AttendanceAlreadyExistsException(UUID employeeId, LocalDate date) {
        super("Attendance already exists for employee: " + employeeId + " on date: " + date);
    }
}
