package com.furkanerd.hr_management_system.exception.custom;

import com.furkanerd.hr_management_system.exception.base.HrValidationException;

public class AlreadyCheckedOutException extends HrValidationException {

    public AlreadyCheckedOutException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return "ALREADY_CHECKED_OUT";
    }
}
