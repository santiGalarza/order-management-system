package com.santiGalarza.order_management.order.core;

import com.santiGalarza.order_management.util.AbstractRepositoryTest;
import com.santiGalarza.order_management.order.status.OrderStatus;
import com.santiGalarza.order_management.order.status.OrderStatusRepository;
import com.santiGalarza.order_management.user.User;
import com.santiGalarza.order_management.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    private User persistUser(String email) {
        User user = User.of(email, "password", "First", "Last");
        return userRepository.save(user);
    }

    private OrderStatus persistOrderStatus(String code) {
        OrderStatus status = OrderStatus.create(code,code,true,false,true);
        return orderStatusRepository.save(status);
    }

    private Order persistOrder(User user, OrderStatus status) {
        Order order = Order.create(BigDecimal.TEN, status, new ArrayList<>(), user);
        return orderRepository.save(order);
    }

    @Nested
    @DisplayName("findByUserId")
    class FindByUserId {

        @Test
        @DisplayName("returns all orders belonging to the user")
        void returnsOrdersBelongingToUser() {
            User user = persistUser("user-" + UUID.randomUUID() + "@email.com");
            OrderStatus status = persistOrderStatus("STATUS-" + UUID.randomUUID());
            Order order = persistOrder(user, status);

            List<Order> result = orderRepository.findByUserId(user.getId());

            assertThat(result).extracting(Order::getId).containsExactly(order.getId());
        }

        @Test
        @DisplayName("returns an empty list when the user has no orders")
        void returnsEmptyListWhenUserHasNoOrders() {
            User user = persistUser("user-" + UUID.randomUUID() + "@email.com");

            List<Order> result = orderRepository.findByUserId(user.getId());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("does not return orders belonging to other users")
        void doesNotReturnOtherUsersOrders() {
            User user = persistUser("user-" + UUID.randomUUID() + "@email.com");
            User otherUser = persistUser("user-" + UUID.randomUUID() + "@email.com");
            OrderStatus status = persistOrderStatus("STATUS-" + UUID.randomUUID());
            persistOrder(otherUser, status);

            List<Order> result = orderRepository.findByUserId(user.getId());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByIdAndUser_Email")
    class FindByIdAndUserEmail {

        @Test
        @DisplayName("returns the order when the id and owner email match")
        void returnsOrderWhenIdAndEmailMatch() {
            String email = "user-" + UUID.randomUUID() + "@email.com";
            User user = persistUser(email);
            OrderStatus status = persistOrderStatus("STATUS-" + UUID.randomUUID());
            Order order = persistOrder(user, status);

            Optional<Order> result = orderRepository.findByIdAndUser_Email(order.getId(), email);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(order.getId());
        }

        @Test
        @DisplayName("returns empty when the order belongs to a different user")
        void returnsEmptyWhenOrderBelongsToDifferentUser() {
            User owner = persistUser("user-" + UUID.randomUUID() + "@email.com");
            String otherEmail = "user-" + UUID.randomUUID() + "@email.com";
            persistUser(otherEmail);
            OrderStatus status = persistOrderStatus("STATUS-" + UUID.randomUUID());
            Order order = persistOrder(owner, status);

            Optional<Order> result = orderRepository.findByIdAndUser_Email(order.getId(), otherEmail);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns empty when no order exists with the given id")
        void returnsEmptyWhenOrderDoesNotExist() {
            String email = "user-" + UUID.randomUUID() + "@email.com";
            persistUser(email);

            Optional<Order> result = orderRepository.findByIdAndUser_Email(UUID.randomUUID(), email);

            assertThat(result).isEmpty();
        }
    }
}
