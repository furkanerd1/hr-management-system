package com.furkanerd.hr_management_system.exception;

public class CircularReferenceException extends RuntimeException {
    public CircularReferenceException(String message) {
        super(message);
    }
}
