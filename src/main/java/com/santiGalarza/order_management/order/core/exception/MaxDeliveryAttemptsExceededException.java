package com.santiGalarza.order_management.order.core.exception;

import com.santiGalarza.order_management.common.exception.ConflictException;

import java.util.UUID;

public class MaxDeliveryAttemptsExceededException extends ConflictException {
    public MaxDeliveryAttemptsExceededException(String message) {
        super(message);
    }
    public MaxDeliveryAttemptsExceededException(UUID id) {
        super(String.format("Order with id %s has exceeded maximum delivery attempts",id));
    }

    @Override
    public String getTitle() {
        return "Maximum Delivery Attempts Exceeded";
    }
}
