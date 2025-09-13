package com.furkanerd.hr_management_system.exception.custom;

import com.furkanerd.hr_management_system.exception.base.HrAuthorizationException;

import java.util.UUID;

public class SelfReviewNotAllowedException extends HrAuthorizationException {

    public SelfReviewNotAllowedException(UUID employeeId) {
        super(String.format("Employee %s cannot perform self review", employeeId));
    }

    @Override
    public String getErrorCode() {
        return "SELF_REVIEW_NOT_ALLOWED";
    }
}
