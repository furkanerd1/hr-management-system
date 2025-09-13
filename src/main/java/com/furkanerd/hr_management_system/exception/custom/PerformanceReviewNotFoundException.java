package com.furkanerd.hr_management_system.exception.custom;

import com.furkanerd.hr_management_system.exception.base.HrNotFoundException;

public class PerformanceReviewNotFoundException extends HrNotFoundException {

    public PerformanceReviewNotFoundException(String message) {
        super("PerformanceReview",message);
    }
    @Override
    public String getErrorCode() {
        return "PERFORMANCE_REVIEW_NOT_FOUND";
    }
}
