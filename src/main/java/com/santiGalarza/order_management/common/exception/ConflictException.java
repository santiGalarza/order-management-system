package com.santiGalarza.order_management.common.exception;

public abstract class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }

    public abstract String getTitle();
}
