package com.santiGalarza.order_management.order.item;

import com.santiGalarza.order_management.category.Category;
import com.santiGalarza.order_management.category.CategoryRepository;
import com.santiGalarza.order_management.order.core.Order;
import com.santiGalarza.order_management.order.core.OrderRepository;
import com.santiGalarza.order_management.order.status.OrderStatus;
import com.santiGalarza.order_management.order.status.OrderStatusRepository;
import com.santiGalarza.order_management.product.Product;
import com.santiGalarza.order_management.product.ProductRepository;
import com.santiGalarza.order_management.user.User;
import com.santiGalarza.order_management.user.UserRepository;
import com.santiGalarza.order_management.util.AbstractRepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ItemRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User persistUser(String email) {
        User user = User.of(email, "password", "First", "Last");
        return userRepository.save(user);
    }

    private Order persistOrder(User user, OrderStatus status) {
        Order order = Order.create(BigDecimal.TEN, status, new ArrayList<>(), user);
        return orderRepository.save(order);
    }

    private OrderStatus persistOrderStatus(String code) {
        OrderStatus status = OrderStatus.create(code,code,true,false,true);
        return orderStatusRepository.save(status);
    }

    private Item persistItem(Order order, Product product, int quantity) {
        Item item = Item.of(order, product, quantity);
        return itemRepository.save(item);
    }

    private Category persistCategory(){
        Category category = Category.create("Name","Description",true,null);
        return categoryRepository.save(category);
    }

    private Product persistProduct() {
        Category category = persistCategory();
        Product product = Product.create(
                "Name",BigDecimal.ONE,
                "SKU123456",
                1,
                100,
                10.0f,
                category,
                true);
        return productRepository.save(product);
    }

    @Nested
    @DisplayName("findByIdAndOrderId")
    class FindByIdAndOrderId {

        @Test
        @DisplayName("returns item if both order and item id are valid")
        void returnsItemIfBothOrderAndItemIdAreValid() {
            User user = persistUser("test@email.com");
            OrderStatus status = persistOrderStatus("Pending");
            Order order = persistOrder(user, status);
            Product product = persistProduct();
            Item item = persistItem(order, product, 1);

            Optional<Item> result = itemRepository.findByIdAndOrderId(item.getId(), order.getId());

            assertThat(result.isPresent()).isTrue();
            assertThat(result.get().getId()).isEqualTo(item.getId());
            assertThat(result.get().getOrder().getId()).isEqualTo(order.getId());
        }

        @Test
        @DisplayName("returns empty if item does not belong to the given order")
        void returnsEmptyIfItemDoesNotBelongToOrder() {
            User user = persistUser("test@email.com");
            OrderStatus status = persistOrderStatus("Pending");
            Order orderOne = persistOrder(user, status);
            Order orderTwo = persistOrder(user, status);
            Product product = persistProduct();
            Item item = persistItem(orderOne, product, 1);

            Optional<Item> result = itemRepository.findByIdAndOrderId(item.getId(), orderTwo.getId());

            assertThat(result).isEmpty();
        }
    }
}
