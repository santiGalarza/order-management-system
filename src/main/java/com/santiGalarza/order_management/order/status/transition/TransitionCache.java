package com.santiGalarza.order_management.order.status.transition;

import com.santiGalarza.order_management.order.status.OrderStatus;
import com.santiGalarza.order_management.order.status.OrderStatusTransitionRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransitionCache {

    private final OrderStatusTransitionRepository transitionRepository;

    public TransitionCache(OrderStatusTransitionRepository transitionRepository) {
        this.transitionRepository = transitionRepository;
    }

    @Cacheable(value = "transitions", key = "#from.id")
    public List<OrderStatusTransition> getAllowedTransitions(OrderStatus from) {
        return transitionRepository.findByFromStatusId(from.getId());
    }
}