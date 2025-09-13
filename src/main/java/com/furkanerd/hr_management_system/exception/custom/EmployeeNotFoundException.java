package com.furkanerd.hr_management_system.exception.custom;


import com.furkanerd.hr_management_system.exception.base.HrNotFoundException;

import java.util.UUID;

public class EmployeeNotFoundException extends HrNotFoundException {

    public EmployeeNotFoundException(UUID employeeId) {
        super("Employee", employeeId);
    }

    public EmployeeNotFoundException(String email) {
        super("Employee", email);
    }

    @Override
    public String getErrorCode() {
        return "EMPLOYEE_NOT_FOUND";
    }
}
