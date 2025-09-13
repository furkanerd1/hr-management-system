package com.furkanerd.hr_management_system.exception.custom;

import com.furkanerd.hr_management_system.exception.base.HrNotFoundException;

import java.util.UUID;

public class PositionNotFoundException extends HrNotFoundException {

    public PositionNotFoundException(UUID positionId) {
        super("Position", positionId);
    }

    public PositionNotFoundException(String message, Throwable cause) {
        super("Position",message);
    }

    @Override
    public String getErrorCode() {
        return "POSITION_NOT_FOUND";
    }
}
