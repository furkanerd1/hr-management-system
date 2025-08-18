package com.furkanerd.hr_management_system.exception;


import java.util.UUID;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException() {
    }

    public EmployeeNotFoundException(Throwable cause) {
        super(cause);
    }

    public EmployeeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmployeeNotFoundException(UUID employeeId) {
        super("Employee note found with id: "+ employeeId);
    }

    public EmployeeNotFoundException(String email) {
        super("Employee note found with email : "+ email);
    }
}
