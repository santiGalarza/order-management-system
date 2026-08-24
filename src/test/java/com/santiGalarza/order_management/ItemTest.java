package com.santiGalarza.order_management;

import com.santiGalarza.order_management.order.item.Item;
import com.santiGalarza.order_management.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ItemTest {

    private Product product;
    private Item item;

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
        item = Item.of(null, product, 5);
    }

    @Nested
    @DisplayName("of")
    class of {

        @Test
        @DisplayName("sets product, order, and quantity")
        void setsFieldsCorrectly() {
            assertThat(item.getProduct()).isEqualTo(product);
            assertThat(item.getQuantity()).isEqualTo(5);
        }

        @Test
        @DisplayName("snapshots unit price from product at creation time")
        void snapshotsUnitPrice() {
            assertThat(item.getUnitPrice()).isEqualByComparingTo(BigDecimal.valueOf(10.00));
        }

        @Test
        @DisplayName("does not reflect later changes to product price")
        void unitPriceIsIndependentOfLaterProductChanges() {
            product.setPrice(BigDecimal.valueOf(999.00));

            assertThat(item.getUnitPrice()).isEqualByComparingTo(BigDecimal.valueOf(10.00));
        }
    }

    @Nested
    @DisplayName("updateQuantity")
    class updateQuantity {

        @Test
        @DisplayName("deducts additional stock when quantity increases")
        void deductsStockOnIncrease() {
            item.updateQuantity(8);

            assertThat(item.getQuantity()).isEqualTo(8);
            assertThat(product.getStockQuantity()).isEqualTo(97);
        }

        @Test
        @DisplayName("restores stock when quantity decreases")
        void restoresStockOnDecrease() {
            item.updateQuantity(2);

            assertThat(item.getQuantity()).isEqualTo(2);
            assertThat(product.getStockQuantity()).isEqualTo(103);
        }

        @Test
        @DisplayName("does not touch stock when quantity is unchanged")
        void leavesStockUntouchedWhenQuantityUnchanged() {
            item.updateQuantity(5);

            assertThat(item.getQuantity()).isEqualTo(5);
            assertThat(product.getStockQuantity()).isEqualTo(100);
        }

        @Test
        @DisplayName("throws when increase exceeds available stock")
        void throwsWhenIncreaseExceedsStock() {
            assertThatThrownBy(() -> item.updateQuantity(1000))
                    .isInstanceOf(com.santiGalarza.order_management.product.exception.InsufficientStockException.class);
        }
    }
}
