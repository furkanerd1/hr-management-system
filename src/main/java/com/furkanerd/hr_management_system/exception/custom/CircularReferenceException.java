package com.furkanerd.hr_management_system.exception.custom;

import com.furkanerd.hr_management_system.exception.base.HrValidationException;

public class CircularReferenceException extends HrValidationException {
    public CircularReferenceException() {
        super("Cannot assign manager. This would create a circular reporting hierarchy.");
    }

    @Override
    public String getErrorCode() {
        return "CIRCULAR_REFERENCE";
    }
}
