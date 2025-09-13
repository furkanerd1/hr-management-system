package com.furkanerd.hr_management_system.exception.base;

import org.springframework.http.HttpStatus;

public abstract class HrManagementException extends  RuntimeException {

    protected HrManagementException(String message) {
        super(message);
    }

    protected HrManagementException(String message, Throwable cause) {
        super(message, cause);
    }

    protected HrManagementException() {
    }

    public abstract HttpStatus getHttpStatus();
    public abstract String getErrorCode();
}
