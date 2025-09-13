package com.furkanerd.hr_management_system.exception.base;

import org.springframework.http.HttpStatus;

public abstract class HrAuthorizationException extends HrManagementException {

    protected HrAuthorizationException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
