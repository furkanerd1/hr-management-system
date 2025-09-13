package com.furkanerd.hr_management_system.exception.custom;

import com.furkanerd.hr_management_system.exception.base.HrAuthorizationException;

public class UnauthorizedActionException extends HrAuthorizationException {

    public UnauthorizedActionException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return "UNAUTHORIZED_ACTION";
    }
}
