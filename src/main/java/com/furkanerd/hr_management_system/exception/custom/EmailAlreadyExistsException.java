package com.furkanerd.hr_management_system.exception.custom;

import com.furkanerd.hr_management_system.exception.base.HrConflictException;

public class EmailAlreadyExistsException extends HrConflictException {

    public EmailAlreadyExistsException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return "EMAIL_ALREADY_EXISTS";
    }
}
