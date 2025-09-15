package com.furkanerd.hr_management_system.exception.custom;

import com.furkanerd.hr_management_system.exception.base.HrManagementException;
import org.springframework.http.HttpStatus;

public class NotificationException extends HrManagementException {

    public NotificationException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getErrorCode() {
        return "NOTIFICATION_ERROR";
    }
}
