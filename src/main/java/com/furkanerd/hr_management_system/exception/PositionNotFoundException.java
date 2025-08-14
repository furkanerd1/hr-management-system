package com.furkanerd.hr_management_system.exception;

import java.util.UUID;

public class PositionNotFoundException extends RuntimeException{
    public PositionNotFoundException() {
    }

    public PositionNotFoundException(UUID positionId) {
        super("Position not found with id : " + positionId);
    }

    public PositionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PositionNotFoundException(Throwable cause) {
        super(cause);
    }
}
