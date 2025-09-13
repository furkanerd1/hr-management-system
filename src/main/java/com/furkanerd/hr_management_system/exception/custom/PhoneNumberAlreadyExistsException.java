package com.furkanerd.hr_management_system.exception.custom;

import com.furkanerd.hr_management_system.exception.base.HrConflictException;

public class PhoneNumberAlreadyExistsException extends HrConflictException {

    public PhoneNumberAlreadyExistsException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return "PHONE_NUMBER_ALREADY_EXISTS";
    }
}
