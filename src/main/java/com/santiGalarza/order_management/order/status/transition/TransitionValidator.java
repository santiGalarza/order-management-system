package com.santiGalarza.order_management.order.status.transition;

import com.santiGalarza.order_management.order.status.OrderStatus;
import com.santiGalarza.order_management.order.status.transition.exception.InvalidOrderStatusTransitionException;
import org.springframework.stereotype.Service;

@Service
public class TransitionValidator {

    private final TransitionCache transitionCache;

    public TransitionValidator(TransitionCache transitionCache) {
        this.transitionCache = transitionCache;
    }

    public void validate(OrderStatus from, OrderStatus to) {
        boolean allowed = transitionCache.getAllowedTransitions(from).stream()
                .anyMatch(t -> t.getToStatus().getId().equals(to.getId()));
        if (!allowed) {
            throw new InvalidOrderStatusTransitionException(from.getCode(), to.getCode());
        }
    }

}
