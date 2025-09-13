package com.furkanerd.hr_management_system.exception.base;

import org.springframework.http.HttpStatus;

public abstract class HrConflictException extends HrManagementException {

    protected HrConflictException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }
}
