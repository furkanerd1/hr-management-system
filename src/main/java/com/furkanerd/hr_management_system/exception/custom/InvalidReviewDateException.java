package com.furkanerd.hr_management_system.exception.custom;

import com.furkanerd.hr_management_system.exception.base.HrValidationException;

public class InvalidReviewDateException extends HrValidationException {

    public InvalidReviewDateException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return "INVALID_REVIEW_DATE";
    }
}
