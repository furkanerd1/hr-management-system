package com.furkanerd.hr_management_system.exception.base;

import org.springframework.http.HttpStatus;

public abstract class HrValidationException extends HrManagementException {

    protected HrValidationException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
