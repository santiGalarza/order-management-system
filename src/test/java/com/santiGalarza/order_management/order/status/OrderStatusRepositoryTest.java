package com.santiGalarza.order_management.order.status;

import com.santiGalarza.order_management.util.AbstractRepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class OrderStatusRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    private OrderStatus persistOrderStatus(String code, boolean isInitial) {
        OrderStatus status = OrderStatus.create(code,code,isInitial,false,true);
        return orderStatusRepository.save(status);
    }

    @Nested
    @DisplayName("findByInitialTrue")
    class FindByInitialTrueTest {

        @Test
        @DisplayName("returns status of the status code with initial equal to true")
        void returnsStatusIfInitialTrue() {
            OrderStatus initialStatus = persistOrderStatus("Pending", true);
            persistOrderStatus("Shipped", false);

            Optional<OrderStatus> result = orderStatusRepository.findByInitialTrue();
            assertThat(result).isPresent();
            assertThat(result.get().getCode()).isEqualTo(initialStatus.getCode());
            assertThat(result.get().getId()).isEqualTo(initialStatus.getId());
        }

        @Test
        @DisplayName("returns empty if no status with initial equal to true exists")
        void returnsEmptyIfNoInitialTrue() {
            persistOrderStatus("Shipped", false);

            Optional<OrderStatus> result = orderStatusRepository.findByInitialTrue();
            assertThat(result).isEmpty();
        }
    }
}
