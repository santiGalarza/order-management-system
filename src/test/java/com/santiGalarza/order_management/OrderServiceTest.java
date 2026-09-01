package com.santiGalarza.order_management;

import com.santiGalarza.order_management.order.Order;
import com.santiGalarza.order_management.order.OrderMapper;
import com.santiGalarza.order_management.order.OrderRepository;
import com.santiGalarza.order_management.order.OrderService;
import com.santiGalarza.order_management.order.dto.CreateOrderRequest;
import com.santiGalarza.order_management.order.dto.OrderResponse;
import com.santiGalarza.order_management.order.exception.OrderNotFoundException;
import com.santiGalarza.order_management.order.exception.OrderNotModifiableException;
import com.santiGalarza.order_management.order.item.Item;
import com.santiGalarza.order_management.order.item.ItemMapper;
import com.santiGalarza.order_management.order.item.ItemRepository;
import com.santiGalarza.order_management.order.item.dto.CreateItemRequest;
import com.santiGalarza.order_management.order.item.dto.ItemResponse;
import com.santiGalarza.order_management.order.item.dto.PatchItemRequest;
import com.santiGalarza.order_management.order.item.exception.ItemNotFoundException;
import com.santiGalarza.order_management.order.status.OrderStatus;
import com.santiGalarza.order_management.order.status.OrderStatusService;
import com.santiGalarza.order_management.order.status.dto.UpdateStatusRequest;
import com.santiGalarza.order_management.product.Product;
import com.santiGalarza.order_management.product.ProductService;
import com.santiGalarza.order_management.user.User;
import com.santiGalarza.order_management.user.UserRepository;
import com.santiGalarza.order_management.user.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductService productService;

    @Mock
    private OrderStatusService orderStatusService;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Product product;
    private OrderStatus orderStatus;

    @BeforeEach
    void setUp() {
        user = User.of("test@email.com", "password", "First", "Last");
        user.setId(UUID.randomUUID());
        product = Product.create("Test Product", BigDecimal.valueOf(10.00), "SKU123456", 1, 100, 0.5f, null, true);
        product.setId(UUID.randomUUID());
        orderStatus = mock(OrderStatus.class);
    }

    private MockedStatic<SecurityContextHolder> mockSecurityContext(String email, boolean isStaff) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(email);
        List<GrantedAuthority> authorities = isStaff
                ? List.of(new SimpleGrantedAuthority("ORDER_READ_ALL"))
                : List.of(new SimpleGrantedAuthority("ORDER_READ"));
        lenient().doReturn(authorities).when(authentication).getAuthorities();
        when(securityContext.getAuthentication()).thenReturn(authentication);

        MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class);
        mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        return mockedStatic;
    }

    private OrderResponse someOrderResponse() {
        return new OrderResponse(UUID.randomUUID(), LocalDateTime.now(), BigDecimal.TEN, "PENDING", "Pending", List.of());
    }

    private ItemResponse someItemResponse() {
        return new ItemResponse(UUID.randomUUID(), UUID.randomUUID(), "Test Product", BigDecimal.TEN, BigDecimal.TEN, 1);
    }

    @Nested
    @DisplayName("getOrders")
    class GetOrders {

        @Test
        @DisplayName("returns all orders mapped to response dtos")
        void returnsAllMappedOrders() {
            Order order = Order.create(BigDecimal.ZERO, orderStatus, new ArrayList<>(), user);
            OrderResponse response = someOrderResponse();

            when(orderRepository.findAll()).thenReturn(List.of(order));
            when(orderMapper.toResponseDto(order)).thenReturn(response);

            List<OrderResponse> result = orderService.getOrders();

            assertThat(result).containsExactly(response);
        }
    }

    @Nested
    @DisplayName("getMyOrders")
    class GetMyOrders {

        @Test
        @DisplayName("returns the caller's orders mapped to response dtos")
        void returnsCallersMappedOrders() {
            Order order = Order.create(BigDecimal.ZERO, orderStatus, new ArrayList<>(), user);
            OrderResponse response = someOrderResponse();

            when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));
            when(orderRepository.findByUserId(user.getId())).thenReturn(List.of(order));
            when(orderMapper.toResponseDto(order)).thenReturn(response);

            List<OrderResponse> result = orderService.getMyOrders("test@email.com");

            assertThat(result).containsExactly(response);
        }

        @Test
        @DisplayName("throws when the caller is not found")
        void throwsWhenCallerNotFound() {
            when(userRepository.findByEmail("missing@email.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.getMyOrders("missing@email.com"))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getOrder")
    class GetOrder {

        @Test
        @DisplayName("staff can fetch any order by id")
        void staffFetchesAnyOrder() {
            Order order = Order.create(BigDecimal.ZERO, orderStatus, new ArrayList<>(), user);
            OrderResponse response = someOrderResponse();
            UUID id = UUID.randomUUID();

            try (MockedStatic<SecurityContextHolder> ignored = mockSecurityContext("staff@email.com", true)) {
                when(orderRepository.findById(id)).thenReturn(Optional.of(order));
                when(orderMapper.toResponseDto(order)).thenReturn(response);

                OrderResponse result = orderService.getOrder(id);

                assertThat(result).isEqualTo(response);
            }
        }

        @Test
        @DisplayName("non-staff can only fetch their own order")
        void nonStaffFetchesOwnOrder() {
            Order order = Order.create(BigDecimal.ZERO, orderStatus, new ArrayList<>(), user);
            OrderResponse response = someOrderResponse();
            UUID id = UUID.randomUUID();

            try (MockedStatic<SecurityContextHolder> ignored = mockSecurityContext("test@email.com", false)) {
                when(orderRepository.findByIdAndUser_Email(id, "test@email.com")).thenReturn(Optional.of(order));
                when(orderMapper.toResponseDto(order)).thenReturn(response);

                OrderResponse result = orderService.getOrder(id);

                assertThat(result).isEqualTo(response);
            }
        }

        @Test
        @DisplayName("throws when order is not found")
        void throwsWhenOrderNotFound() {
            UUID id = UUID.randomUUID();

            try (MockedStatic<SecurityContextHolder> ignored = mockSecurityContext("test@email.com", false)) {
                when(orderRepository.findByIdAndUser_Email(id, "test@email.com")).thenReturn(Optional.empty());

                assertThatThrownBy(() -> orderService.getOrder(id))
                        .isInstanceOf(OrderNotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayName("createOrder")
    class CreateOrder {

        @Test
        @DisplayName("creates the order, deducts stock, and returns the mapped result")
        void createsOrderAndDeductsStock() {
            CreateItemRequest itemRequest = new CreateItemRequest();
            itemRequest.setProductId(product.getId());
            itemRequest.setQuantity(5);

            CreateOrderRequest request = new CreateOrderRequest();
            request.setItems(List.of(itemRequest));

            OrderResponse response = someOrderResponse();

            Map<UUID, Product> productMap = new HashMap<>();
            productMap.put(product.getId(), product);

            Order orderEntity = Order.create(BigDecimal.ZERO, null, new ArrayList<>(), null);

            when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));
            when(productService.getProductsByIds(List.of(product.getId()))).thenReturn(productMap);
            when(orderMapper.toEntity(request)).thenReturn(orderEntity);
            when(orderStatusService.getInitialStatus()).thenReturn(orderStatus);
            when(orderRepository.save(orderEntity)).thenReturn(orderEntity);
            when(orderMapper.toResponseDto(orderEntity)).thenReturn(response);

            OrderResponse result = orderService.createOrder(request, "test@email.com");

            assertThat(product.getStockQuantity()).isEqualTo(95);
            assertThat(orderEntity.getItems()).hasSize(1);
            assertThat(orderEntity.getCurrentStatus()).isEqualTo(orderStatus);
            assertThat(orderEntity.getUser()).isEqualTo(user);
            assertThat(orderEntity.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("throws when the user is not found")
        void throwsWhenUserNotFound() {
            CreateOrderRequest request = new CreateOrderRequest();
            request.setItems(List.of());

            when(userRepository.findByEmail("missing@email.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.createOrder(request, "missing@email.com"))
                    .isInstanceOf(UserNotFoundException.class);

            verify(orderRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateStatus")
    class UpdateStatus {

        @Test
        @DisplayName("transitions the order and returns the mapped result")
        void transitionsOrderAndReturnsMapped() {
            Order order = Order.create(BigDecimal.ZERO, orderStatus, new ArrayList<>(), user);

            UpdateStatusRequest request = new UpdateStatusRequest();
            request.setStatusCode("SHIPPED");
            request.setNotes("On its way");

            OrderResponse response = someOrderResponse();
            UUID id = UUID.randomUUID();

            when(orderRepository.findById(id)).thenReturn(Optional.of(order));
            when(orderRepository.save(order)).thenReturn(order);
            when(orderMapper.toResponseDto(order)).thenReturn(response);

            try (MockedStatic<SecurityContextHolder> ignored = mockSecurityContext("test@email.com", false)) {
                when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));

                OrderResponse result = orderService.updateStatus(id, request);

                verify(orderStatusService).transition(order, "SHIPPED", user.getId(), "On its way");
                assertThat(result).isEqualTo(response);
            }
        }

        @Test
        @DisplayName("throws when order is not found")
        void throwsWhenOrderNotFound() {
            UUID id = UUID.randomUUID();
            UpdateStatusRequest request = new UpdateStatusRequest();
            request.setStatusCode("SHIPPED");

            when(orderRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.updateStatus(id, request))
                    .isInstanceOf(OrderNotFoundException.class);
        }

        @Test
        @DisplayName("throws when the caller is not found")
        void throwsWhenCallerNotFound() {
            Order order = Order.create(BigDecimal.ZERO, orderStatus, new ArrayList<>(), user);
            UpdateStatusRequest request = new UpdateStatusRequest();
            request.setStatusCode("SHIPPED");
            UUID id = UUID.randomUUID();

            when(orderRepository.findById(id)).thenReturn(Optional.of(order));

            try (MockedStatic<SecurityContextHolder> ignored = mockSecurityContext("missing@email.com", false)) {
                when(userRepository.findByEmail("missing@email.com")).thenReturn(Optional.empty());

                assertThatThrownBy(() -> orderService.updateStatus(id, request))
                        .isInstanceOf(UserNotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayName("deleteOrder")
    class DeleteOrder {

        @Test
        @DisplayName("staff can delete an order regardless of modifiable status")
        void staffDeletesRegardlessOfStatus() {
            Order order = Order.create(BigDecimal.ZERO, orderStatus, new ArrayList<>(), user);
            UUID id = UUID.randomUUID();

            try (MockedStatic<SecurityContextHolder> ignored = mockSecurityContext("staff@email.com", true)) {
                when(orderRepository.findById(id)).thenReturn(Optional.of(order));

                orderService.deleteOrder(id);

                verify(orderRepository).deleteById(id);
            }
        }

        @Test
        @DisplayName("non-staff cannot delete a non-modifiable order")
        void nonStaffCannotDeleteNonModifiableOrder() {
            Order order = Order.create(BigDecimal.ZERO, orderStatus, new ArrayList<>(), user);
            UUID id = UUID.randomUUID();

            try (MockedStatic<SecurityContextHolder> ignored = mockSecurityContext("test@email.com", false)) {
                when(orderRepository.findByIdAndUser_Email(id, "test@email.com")).thenReturn(Optional.of(order));
                when(orderStatus.isModifiable()).thenReturn(false);

                assertThatThrownBy(() -> orderService.deleteOrder(id))
                        .isInstanceOf(OrderNotModifiableException.class);

                verify(orderRepository, never()).deleteById(any());
            }
        }

        @Test
        @DisplayName("throws when order is not found")
        void throwsWhenOrderNotFound() {
            UUID id = UUID.randomUUID();

            try (MockedStatic<SecurityContextHolder> ignored = mockSecurityContext("test@email.com", false)) {
                when(orderRepository.findByIdAndUser_Email(id, "test@email.com")).thenReturn(Optional.empty());

                assertThatThrownBy(() -> orderService.deleteOrder(id))
                        .isInstanceOf(OrderNotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayName("getItems")
    class GetItems {

        @Test
        @DisplayName("returns the order's items mapped to response dtos")
        void returnsMappedItems() {
            Order order = Order.create(BigDecimal.ZERO, orderStatus, new ArrayList<>(), user);
            Item item = Item.of(order, product, 2);
            order.getItems().add(item);
            ItemResponse response = someItemResponse();
            UUID id = UUID.randomUUID();

            try (MockedStatic<SecurityContextHolder> ignored = mockSecurityContext("staff@email.com", true)) {
                when(orderRepository.findById(id)).thenReturn(Optional.of(order));
                when(itemMapper.toResponseDto(item)).thenReturn(response);

                List<ItemResponse> result = orderService.getItems(id);

                assertThat(result).containsExactly(response);
            }
        }
    }

    @Nested
    @DisplayName("getItem")
    class GetItem {

        @Test
        @DisplayName("returns the mapped item when found")
        void returnsMappedItemWhenFound() {
            Order order = Order.create(BigDecimal.ZERO, orderStatus, new ArrayList<>(), user);
            Item item = Item.of(order, product, 2);
            ItemResponse response = someItemResponse();
            UUID id = UUID.randomUUID();
            UUID itemId = UUID.randomUUID();

            try (MockedStatic<SecurityContextHolder> ignored = mockSecurityContext("staff@email.com", true)) {
                when(orderRepository.findById(id)).thenReturn(Optional.of(order));
                when(itemRepository.findByIdAndOrderId(itemId, id)).thenReturn(Optional.of(item));
                when(itemMapper.toResponseDto(item)).thenReturn(response);

                ItemResponse result = orderService.getItem(id, itemId);

                assertThat(result).isEqualTo(response);
            }
        }

        @Test
        @DisplayName("throws when item is not found")
        void throwsWhenItemNotFound() {
            Order order = Order.create(BigDecimal.ZERO, orderStatus, new ArrayList<>(), user);
            UUID id = UUID.randomUUID();
            UUID itemId = UUID.randomUUID();

            try (MockedStatic<SecurityContextHolder> ignored = mockSecurityContext("staff@email.com", true)) {
                when(orderRepository.findById(id)).thenReturn(Optional.of(order));
                when(itemRepository.findByIdAndOrderId(itemId, id)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> orderService.getItem(id, itemId))
                        .isInstanceOf(ItemNotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayName("createItem")
    class CreateItem {

        @Test
        @DisplayName("adds the item, deducts stock, and returns the mapped result")
        void addsItemAndDeductsStock() {
            Order order = Order.create(BigDecimal.ZERO, orderStatus, new ArrayList<>(), user);

            CreateItemRequest request = new CreateItemRequest();
            request.setProductId(product.getId());
            request.setQuantity(3);

            ItemResponse response = someItemResponse();
            UUID id = UUID.randomUUID();

            try (MockedStatic<SecurityContextHolder> ignored = mockSecurityContext("staff@email.com", true)) {
                when(orderRepository.findById(id)).thenReturn(Optional.of(order));
                when(productService.findProduct(product.getId())).thenReturn(product);
                when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
                when(itemMapper.toResponseDto(any(Item.class))).thenReturn(response);

                ItemResponse result = orderService.createItem(id, request);

                assertThat(product.getStockQuantity()).isEqualTo(97);
                assertThat(order.getItems()).hasSize(1);
                assertThat(order.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(30.00));
                assertThat(result).isEqualTo(response);
            }
        }

        @Test
        @DisplayName("throws when order is not modifiable for a non-staff caller")
        void throwsWhenOrderNotModifiable() {
            Order order = Order.create(BigDecimal.ZERO, orderStatus, new ArrayList<>(), user);
            CreateItemRequest request = new CreateItemRequest();
            request.setProductId(product.getId());
            request.setQuantity(1);
            UUID id = UUID.randomUUID();

            try (MockedStatic<SecurityContextHolder> ignored = mockSecurityContext("test@email.com", false)) {
                when(orderRepository.findByIdAndUser_Email(id, "test@email.com")).thenReturn(Optional.of(order));
                when(orderStatus.isModifiable()).thenReturn(false);

                assertThatThrownBy(() -> orderService.createItem(id, request))
                        .isInstanceOf(OrderNotModifiableException.class);
            }
        }
    }

    @Nested
    @DisplayName("updateItemQuantity")
    class UpdateItemQuantity {

        @Test
        @DisplayName("updates the quantity, recalculates the total, and returns the mapped result")
        void updatesQuantityAndRecalculatesTotal() {
            Order order = Order.create(BigDecimal.ZERO, orderStatus, new ArrayList<>(), user);
            Item item = Item.of(order, product, 2);
            order.getItems().add(item);
            order.recalculateTotalPrice();

            PatchItemRequest request = new PatchItemRequest();
            request.setQuantity(5);

            ItemResponse response = someItemResponse();
            UUID id = UUID.randomUUID();
            UUID itemId = UUID.randomUUID();

            try (MockedStatic<SecurityContextHolder> ignored = mockSecurityContext("staff@email.com", true)) {
                when(orderRepository.findById(id)).thenReturn(Optional.of(order));
                when(itemRepository.findByIdAndOrderId(itemId, id)).thenReturn(Optional.of(item));
                when(itemRepository.save(item)).thenReturn(item);
                when(itemMapper.toResponseDto(item)).thenReturn(response);

                ItemResponse result = orderService.updateItemQuantity(id, itemId, request);

                assertThat(item.getQuantity()).isEqualTo(5);
                assertThat(order.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
                assertThat(result).isEqualTo(response);
            }
        }

        @Test
        @DisplayName("throws when item is not found")
        void throwsWhenItemNotFound() {
            Order order = Order.create(BigDecimal.ZERO, orderStatus, new ArrayList<>(), user);
            PatchItemRequest request = new PatchItemRequest();
            request.setQuantity(5);
            UUID id = UUID.randomUUID();
            UUID itemId = UUID.randomUUID();

            try (MockedStatic<SecurityContextHolder> ignored = mockSecurityContext("staff@email.com", true)) {
                when(orderRepository.findById(id)).thenReturn(Optional.of(order));
                when(itemRepository.findByIdAndOrderId(itemId, id)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> orderService.updateItemQuantity(id, itemId, request))
                        .isInstanceOf(ItemNotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayName("deleteItem")
    class DeleteItem {

        @Test
        @DisplayName("deletes the item and recalculates the total")
        void deletesItemAndRecalculatesTotal() {
            Order order = Order.create(BigDecimal.ZERO, orderStatus, new ArrayList<>(), user);
            Item item = Item.of(order, product, 2);
            order.getItems().add(item);
            order.recalculateTotalPrice();

            UUID id = UUID.randomUUID();
            UUID itemId = UUID.randomUUID();

            try (MockedStatic<SecurityContextHolder> ignored = mockSecurityContext("staff@email.com", true)) {
                when(orderRepository.findById(id)).thenReturn(Optional.of(order));
                when(itemRepository.findByIdAndOrderId(itemId, id)).thenReturn(Optional.of(item));

                orderService.deleteItem(id, itemId);

                verify(itemRepository).delete(item);
            }
        }

        @Test
        @DisplayName("throws when order is not modifiable for a non-staff caller")
        void throwsWhenOrderNotModifiable() {
            Order order = Order.create(BigDecimal.ZERO, orderStatus, new ArrayList<>(), user);
            UUID id = UUID.randomUUID();
            UUID itemId = UUID.randomUUID();

            try (MockedStatic<SecurityContextHolder> ignored = mockSecurityContext("test@email.com", false)) {
                when(orderRepository.findByIdAndUser_Email(id, "test@email.com")).thenReturn(Optional.of(order));
                when(orderStatus.isModifiable()).thenReturn(false);

                assertThatThrownBy(() -> orderService.deleteItem(id, itemId))
                        .isInstanceOf(OrderNotModifiableException.class);

                verify(itemRepository, never()).delete(any());
            }
        }
    }
}