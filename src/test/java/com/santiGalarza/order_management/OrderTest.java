package com.santiGalarza.order_management;

import com.santiGalarza.order_management.order.Order;
import com.santiGalarza.order_management.order.item.Item;
import com.santiGalarza.order_management.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class OrderTest {

    private Product product;
    private Order order;

    @BeforeEach
    void setUp() {
        product = Product.create(
                "Test Product",
                BigDecimal.valueOf(10.00),
                "SKU123456",
                1,
                100,
                0.5f,
                null,
                true
        );
        order = Order.create(BigDecimal.ZERO, null, new ArrayList<>(), null);
    }

    @Nested
    @DisplayName("create")
    class create {

        @Test
        @DisplayName("sets totalPrice, currentStatus, items, and user")
        void setsFieldsCorrectly() {
            List<Item> items = List.of(Item.of(null, product, 3));

            Order created = Order.create(BigDecimal.valueOf(30.00), null, items, null);

            assertThat(created.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(30.00));
            assertThat(created.getItems()).containsExactlyElementsOf(items);
        }

        @Test
        @DisplayName("does not derive totalPrice from items")
        void totalPriceIsNotDerivedFromItems() {
            List<Item> items = List.of(Item.of(null, product, 3));

            Order created = Order.create(BigDecimal.valueOf(999.00), null, items, null);

            assertThat(created.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(999.00));
        }
    }

    @Nested
    @DisplayName("recalculateTotalPrice")
    class recalculateTotalPrice {

        @Test
        @DisplayName("sums unitPrice times quantity across all items")
        void sumsItemsCorrectly() {
            Product secondProduct = Product.create(
                    "Second Product",
                    BigDecimal.valueOf(5.00),
                    "SKU654321",
                    1,
                    50,
                    0.2f,
                    null,
                    true
            );

            order.getItems().add(Item.of(null, product, 3));
            order.getItems().add(Item.of(null, secondProduct, 2));

            order.recalculateTotalPrice();

            assertThat(order.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(40.00));
        }

        @Test
        @DisplayName("returns zero when there are no items")
        void returnsZeroForEmptyItems() {
            order.recalculateTotalPrice();

            assertThat(order.getTotalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("reflects an item's updated quantity")
        void reflectsUpdatedItemQuantity() {
            Item item = Item.of(null, product, 3);
            order.getItems().add(item);

            item.updateQuantity(5);
            order.recalculateTotalPrice();

            assertThat(order.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
        }
    }

    @Nested
    @DisplayName("incrementDeliveryAttempts")
    class incrementDeliveryAttempts {

        @Test
        @DisplayName("increases deliveryAttempts by one")
        void incrementsByOne() {
            order.incrementDeliveryAttempts();

            assertThat(order.getDeliveryAttempts()).isEqualTo(1);
        }

        @Test
        @DisplayName("accumulates across multiple calls")
        void accumulatesAcrossMultipleCalls() {
            order.incrementDeliveryAttempts();
            order.incrementDeliveryAttempts();
            order.incrementDeliveryAttempts();

            assertThat(order.getDeliveryAttempts()).isEqualTo(3);
        }
    }
}
