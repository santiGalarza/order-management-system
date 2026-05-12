package com.santiGalarza.order_management.order;

public class InvalidOrderStatusTransitionException extends RuntimeException {
    public InvalidOrderStatusTransitionException(String message) {
        super(message);
    }
    public InvalidOrderStatusTransitionException(Status from, Status to) {
      super(String.format("Cannot transition from %s to %s", from, to));
    }
}
