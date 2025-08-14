package com.furkanerd.hr_management_system.exception;


import java.util.UUID;

public class DepartmentNotFoundException extends RuntimeException {

    public DepartmentNotFoundException() {
    }

    public DepartmentNotFoundException(UUID departmentId) {
        super("Department not found with id: " + departmentId);
    }

    public DepartmentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DepartmentNotFoundException(Throwable cause) {
        super(cause);
    }
}
