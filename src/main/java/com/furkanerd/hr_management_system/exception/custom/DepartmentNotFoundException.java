package com.furkanerd.hr_management_system.exception.custom;


import com.furkanerd.hr_management_system.exception.base.HrNotFoundException;

import java.util.UUID;

public class DepartmentNotFoundException extends HrNotFoundException {

    public DepartmentNotFoundException(UUID departmentId) {
        super("Department", departmentId);
    }

    public DepartmentNotFoundException(String message, Throwable cause) {
        super("Department",message);
    }

    @Override
    public String getErrorCode() {
        return "DEPARTMENT_NOT_FOUND";
    }
}
