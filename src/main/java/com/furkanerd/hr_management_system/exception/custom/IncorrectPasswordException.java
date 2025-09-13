package com.furkanerd.hr_management_system.exception.custom;

import com.furkanerd.hr_management_system.exception.base.HrValidationException;

public class IncorrectPasswordException extends HrValidationException {

    public IncorrectPasswordException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return "INCORRECT_PASSWORD";
    }
}
