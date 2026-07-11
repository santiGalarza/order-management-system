package com.santiGalarza.order_management.order.status;

import java.util.Set;

public enum Status {
    PENDING(true),
    CONFIRMED(false),
    SHIPPED(false),
    DELIVERED(false),
    RETURNED(false),
    DELIVERY_FAILED(false),
    REATTEMPTING_DELIVERY(false),
    CANCELLED(false);

    private Set<Status> allowedTransitions;
    private final boolean modifiable;

    static {
        PENDING.allowedTransitions = Set.of(CONFIRMED, CANCELLED);
        CONFIRMED.allowedTransitions = Set.of(SHIPPED, CANCELLED);
        SHIPPED.allowedTransitions = Set.of(DELIVERED,DELIVERY_FAILED);
        DELIVERED.allowedTransitions = Set.of(RETURNED);
        RETURNED.allowedTransitions = Set.of();
        DELIVERY_FAILED.allowedTransitions = Set.of(REATTEMPTING_DELIVERY,CANCELLED);
        REATTEMPTING_DELIVERY.allowedTransitions = Set.of(DELIVERED,DELIVERY_FAILED,CANCELLED);
        CANCELLED.allowedTransitions = Set.of();
    }

    Status(boolean modifiable){
        this.modifiable = modifiable;
    }

    public boolean isModifiable() {
        return modifiable;
    }

    public void validateTransition(Status next) {
        if (!allowedTransitions.contains(next)) {
            throw new InvalidOrderStatusTransitionException(this, next);
        }
    }

    public Set<Status> getAllowedTransitions() {
        return allowedTransitions;
    }
}
