package com.furkanerd.hr_management_system.exception;

public class SelfReviewNotAllowedException extends RuntimeException {
    public SelfReviewNotAllowedException(String message) {
        super(message);
    }
}
