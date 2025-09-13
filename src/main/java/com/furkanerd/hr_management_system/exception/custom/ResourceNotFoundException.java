package com.furkanerd.hr_management_system.exception.custom;

import com.furkanerd.hr_management_system.exception.base.HrNotFoundException;

public class ResourceNotFoundException extends HrNotFoundException {

    public ResourceNotFoundException(String message) {
        super("Resource",message);
    }

    @Override
    public String getErrorCode() {
        return "RESOURCE_NOT_FOUND";
    }
}
