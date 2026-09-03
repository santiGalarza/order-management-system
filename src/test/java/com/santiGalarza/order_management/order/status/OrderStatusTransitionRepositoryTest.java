package com.santiGalarza.order_management.order.status;

import com.santiGalarza.order_management.util.AbstractRepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderStatusTransitionRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private OrderStatusTransitionRepository orderStatusTransitionRepository;

    private OrderStatus persistOrderStatus(String code) {
        OrderStatus status = OrderStatus.create(code, code, false, false, true);
        return orderStatusRepository.save(status);
    }

    private OrderStatusTransition persistTransition(OrderStatus from, OrderStatus to) {
        OrderStatusTransition transition = OrderStatusTransition.create(from, to, null);
        return orderStatusTransitionRepository.save(transition);
    }

    @Nested
    @DisplayName("findByFromStatusIdAndToStatusId")
    class FindByFromStatusIdAndToStatusId {

        @Test
        @DisplayName("returns transition when a valid from-to pair exists")
        void returnsTransitionWhenValidPairExists() {
            OrderStatus pending = persistOrderStatus("PENDING");
            OrderStatus confirmed = persistOrderStatus("CONFIRMED");
            OrderStatusTransition transition = persistTransition(pending, confirmed);

            Optional<OrderStatusTransition> result =
                    orderStatusTransitionRepository.findByFromStatusIdAndToStatusId(pending.getId(), confirmed.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(transition.getId());
        }

        @Test
        @DisplayName("returns empty when no transition exists between the given statuses")
        void returnsEmptyWhenNoTransitionExists() {
            OrderStatus pending = persistOrderStatus("PENDING");
            OrderStatus cancelled = persistOrderStatus("CANCELLED");

            Optional<OrderStatusTransition> result =
                    orderStatusTransitionRepository.findByFromStatusIdAndToStatusId(pending.getId(), cancelled.getId());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns empty when statuses are reversed relative to a persisted transition")
        void returnsEmptyWhenDirectionIsReversed() {
            OrderStatus pending = persistOrderStatus("PENDING");
            OrderStatus confirmed = persistOrderStatus("CONFIRMED");
            persistTransition(pending, confirmed);

            Optional<OrderStatusTransition> result =
                    orderStatusTransitionRepository.findByFromStatusIdAndToStatusId(confirmed.getId(), pending.getId());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByFromStatusId")
    class FindByFromStatusId {

        @Test
        @DisplayName("returns all transitions originating from the given status")
        void returnsAllTransitionsFromGivenStatus() {
            OrderStatus pending = persistOrderStatus("PENDING");
            OrderStatus confirmed = persistOrderStatus("CONFIRMED");
            OrderStatus cancelled = persistOrderStatus("CANCELLED");
            OrderStatusTransition toConfirmed = persistTransition(pending, confirmed);
            OrderStatusTransition toCancelled = persistTransition(pending, cancelled);

            List<OrderStatusTransition> result =
                    orderStatusTransitionRepository.findByFromStatusId(pending.getId());

            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(OrderStatusTransition::getId)
                    .containsExactlyInAnyOrder(toConfirmed.getId(), toCancelled.getId());
        }

        @Test
        @DisplayName("excludes transitions originating from a different status")
        void excludesTransitionsFromDifferentStatus() {
            OrderStatus pending = persistOrderStatus("PENDING");
            OrderStatus confirmed = persistOrderStatus("CONFIRMED");
            OrderStatus shipped = persistOrderStatus("SHIPPED");
            persistTransition(pending, confirmed);
            OrderStatusTransition confirmedToShipped = persistTransition(confirmed, shipped);

            List<OrderStatusTransition> result =
                    orderStatusTransitionRepository.findByFromStatusId(confirmed.getId());

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(confirmedToShipped.getId());
        }

        @Test
        @DisplayName("returns empty list when the status has no outgoing transitions")
        void returnsEmptyListWhenNoOutgoingTransitions() {
            OrderStatus delivered = persistOrderStatus("DELIVERED");

            List<OrderStatusTransition> result =
                    orderStatusTransitionRepository.findByFromStatusId(delivered.getId());

            assertThat(result).isEmpty();
        }
    }
}
