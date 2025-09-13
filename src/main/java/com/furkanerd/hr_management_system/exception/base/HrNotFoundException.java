package com.furkanerd.hr_management_system.exception.base;

import org.springframework.http.HttpStatus;

public abstract class HrNotFoundException extends HrManagementException {

    protected HrNotFoundException(String resourceType, Object identifier) {
        super(String.format("%s not found with identifier: %s", resourceType, identifier));
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
