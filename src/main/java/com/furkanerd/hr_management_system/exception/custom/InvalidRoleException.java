package com.furkanerd.hr_management_system.exception.custom;

import com.furkanerd.hr_management_system.exception.base.HrValidationException;

public class InvalidRoleException extends HrValidationException {

    public InvalidRoleException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return "INVALID_ROLE";
    }
}
