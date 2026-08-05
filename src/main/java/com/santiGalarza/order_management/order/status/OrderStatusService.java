package com.santiGalarza.order_management.order.status;

import com.santiGalarza.order_management.order.MaxDeliveryAttemptsExceededException;
import com.santiGalarza.order_management.order.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class OrderStatusService {

    private final OrderStatusRepository statusRepository;
    private final OrderStatusTransitionRepository transitionRepository;
    private final OrderStatusHistoryRepository historyRepository;

    public OrderStatusService(
            OrderStatusRepository statusRepository,
            OrderStatusTransitionRepository transitionRepository,
            OrderStatusHistoryRepository historyRepository) {
        this.statusRepository = statusRepository;
        this.transitionRepository = transitionRepository;
        this.historyRepository = historyRepository;
    }

    @Transactional
    public void transition(Order order, String toCode, UUID changedBy, String notes) {
        OrderStatus current = order.getCurrentStatus();
        OrderStatus next = statusRepository.findByCode(toCode)
                .orElseThrow(() -> new OrderStatusNotFoundException(toCode));

        transitionRepository.findByFromStatusIdAndToStatusId(current.getId(), next.getId())
                .orElseThrow(() -> new InvalidOrderStatusTransitionException(current.getCode(), next.getCode()));

        if (toCode.equals(StatusCodes.REATTEMPTING_DELIVERY)) {
            int maxAttempts = Integer.parseInt(next.getMetadata("max_attempts"));
            if (order.getDeliveryAttempts() >= maxAttempts) {
                throw new MaxDeliveryAttemptsExceededException(order.getId());
            }
            order.incrementDeliveryAttempts();
        }

        if (toCode.equals(StatusCodes.CANCELLED) || toCode.equals(StatusCodes.RETURN_CONFIRMED)) {
            order.getItems().forEach(item ->
                    item.getProduct().restoreStock(item.getQuantity()));
        }

        historyRepository.save(OrderStatusHistory.of(order, current, next, changedBy, notes));
        order.setCurrentStatus(next);
    }

    public List<OrderStatusTransition> getAvailableTransitions(UUID fromStatusId) {
        return transitionRepository.findByFromStatusId(fromStatusId);
    }

    public List<OrderStatusHistory> getHistory(UUID orderId) {
        return historyRepository.findByOrderIdOrderByCreatedAtAsc(orderId);
    }

    public OrderStatus getInitialStatus() {
        return statusRepository.findByIsInitialTrue()
                .orElseThrow(() -> new IllegalStateException("No initial status configured"));
    }

}
