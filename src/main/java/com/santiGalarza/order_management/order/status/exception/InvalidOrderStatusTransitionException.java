package com.santiGalarza.order_management.order.status.exception;

import com.santiGalarza.order_management.common.exception.ConflictException;

public class InvalidOrderStatusTransitionException extends ConflictException {
    public InvalidOrderStatusTransitionException(String from, String to) {
        super("Invalid status transition: " + from + " to " + to);
    }

    @Override
    public String getTitle() {
        return "Invalid Status Transition";
    }
}
