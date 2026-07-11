package com.santiGalarza.order_management.order;

import com.santiGalarza.order_management.common.exception.ConflictException;

public class InvalidOrderStatusTransitionException extends ConflictException {
    public InvalidOrderStatusTransitionException(String message) {
        super(message);
    }
    public InvalidOrderStatusTransitionException(Status from, Status to) {
      super(String.format("Cannot transition from %s to %s", from, to));
    }

    @Override
    public String getTitle() {
        return "Invalid Status Transition";
    }
}
