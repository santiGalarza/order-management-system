package com.santiGalarza.order_management.order;

import java.util.UUID;

public class MaxDeliveryAttemptsExceededException extends RuntimeException {
    public MaxDeliveryAttemptsExceededException(String message) {
        super(message);
    }
    public MaxDeliveryAttemptsExceededException(UUID id) {
        super(String.format("Order with id %s has exceeded maximum delivery attempts",id));
    }
}
