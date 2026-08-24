package com.santiGalarza.order_management;

import com.santiGalarza.order_management.product.Product;
import com.santiGalarza.order_management.product.exception.InsufficientStockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ProductTest {

    private Product product;

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
    }

    @Nested
    @DisplayName("deductStock")
    class deductStock {

        @Test
        @DisplayName("reduces stock by the given quantity")
        void reducesStockCorrectly() {
            product.deductStock(30);
            assertThat(product.getStockQuantity()).isEqualTo(70);
        }

        @Test
        @DisplayName("allows deducting entire stock")
        void allowsDeductingEntireStock() {
            product.deductStock(100);
            assertThat(product.getStockQuantity()).isEqualTo(0);
        }

        @Test
        @DisplayName("throws when quantity exceeds stock")
        void throwsWhenInsufficientStock() {
            assertThatThrownBy(() -> product.deductStock(101))
                    .isInstanceOf(InsufficientStockException.class);
        }
    }

    @Nested
    @DisplayName("restoreStock")
    class restoreStock {

        @Test
        @DisplayName("increases stock by the given quantity")
        void increasesStockCorrectly() {
            product.restoreStock(10);
            assertThat(product.getStockQuantity()).isEqualTo(110);
        }

        @Test
        @DisplayName("restores stock after a deduction")
        void restoresStockAfterADeduction() {
            product.deductStock(30);
            product.restoreStock(20);
            assertThat(product.getStockQuantity()).isEqualTo(90);
        }
    }
}
