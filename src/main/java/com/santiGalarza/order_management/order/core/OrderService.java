package com.santiGalarza.order_management.order.core;

import com.santiGalarza.order_management.order.core.dto.CreateOrderRequest;
import com.santiGalarza.order_management.order.core.dto.OrderResponse;
import com.santiGalarza.order_management.order.core.exception.OrderNotFoundException;
import com.santiGalarza.order_management.order.core.exception.OrderNotModifiableException;
import com.santiGalarza.order_management.order.item.*;
import com.santiGalarza.order_management.order.item.dto.CreateItemRequest;
import com.santiGalarza.order_management.order.item.dto.ItemResponse;
import com.santiGalarza.order_management.order.item.dto.PatchItemRequest;
import com.santiGalarza.order_management.order.item.exception.ItemNotFoundException;
import com.santiGalarza.order_management.order.status.OrderStatusService;
import com.santiGalarza.order_management.order.status.dto.UpdateStatusRequest;
import com.santiGalarza.order_management.product.Product;
import com.santiGalarza.order_management.product.ProductService;
import com.santiGalarza.order_management.user.User;
import com.santiGalarza.order_management.user.exception.UserNotFoundException;
import com.santiGalarza.order_management.user.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private static final String ORDER_READ_ALL = "ORDER_READ_ALL";

    private final OrderMapper orderMapper;
    private final ItemMapper itemMapper;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final OrderStatusService orderStatusService;

    public OrderService(
            OrderMapper orderMapper, OrderRepository orderRepository,
            ProductService productService, ItemRepository itemRepository, ItemMapper itemMapper, UserRepository userRepository, OrderStatusService orderStatusService) {
        this.orderMapper = orderMapper;
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.productService = productService;
        this.userRepository = userRepository;
        this.orderStatusService = orderStatusService;
    }

    // Order service methods

    public List<OrderResponse> getOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getMyOrders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return orderRepository.findByUserId(user.getId())
                .stream()
                .map(orderMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrder(UUID id) {
        Order order = resolveOrderForCaller(id, currentUserEmail(), isStaff());
        return orderMapper.toResponseDto(order);
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        List<UUID> productIds = request.getItems()
                .stream()
                .map(CreateItemRequest::getProductId)
                .distinct()
                .toList();

        Map<UUID, Product> productMap = productService.getProductsByIds(productIds);

        Order order = orderMapper.toEntity(request);
        order.setCurrentStatus(orderStatusService.getInitialStatus());

        for (CreateItemRequest itemRequest : request.getItems()) {
            Product product = productMap.get(itemRequest.getProductId());
            product.deductStock(itemRequest.getQuantity());
            order.getItems().add(Item.of(order, product, itemRequest.getQuantity()));
        }

        order.setUser(user);
        order.recalculateTotalPrice();
        return orderMapper.toResponseDto(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse updateStatus(UUID id, UpdateStatusRequest request) {
        Order order = findOrder(id);

        String email = currentUserEmail();
        User changedBy = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        orderStatusService.transition(order, request.getStatusCode(), changedBy.getId(), request.getNotes());
        return orderMapper.toResponseDto(orderRepository.save(order));
    }

    @Transactional
    public void deleteOrder(UUID id) {
        boolean staff = isStaff();
        Order order = resolveOrderForCaller(id, currentUserEmail(), staff);
        validateOrderIsModifiable(order, staff);
        orderRepository.deleteById(id);
    }

    // Item Class service methods

    public List<ItemResponse> getItems(UUID id) {
        Order order = resolveOrderForCaller(id, currentUserEmail(), isStaff());
        return order.getItems()
                .stream()
                .map(itemMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public ItemResponse getItem(UUID id, UUID itemId) {
        resolveOrderForCaller(id, currentUserEmail(), isStaff());
        return itemMapper.toResponseDto(findItem(id, itemId));
    }

    @Transactional
    public ItemResponse createItem(UUID id, CreateItemRequest request) {
        boolean staff = isStaff();
        Order order = resolveOrderForCaller(id, currentUserEmail(), staff);
        validateOrderIsModifiable(order, staff);

        Product product = productService.findProduct(request.getProductId());
        product.deductStock(request.getQuantity());

        Item item = Item.of(order, product, request.getQuantity());
        order.getItems().add(item);
        order.recalculateTotalPrice();

        return itemMapper.toResponseDto(itemRepository.save(item));
    }

    @Transactional
    public ItemResponse updateItemQuantity(UUID id, UUID itemId, PatchItemRequest request) {
        boolean staff = isStaff();
        Order order = resolveOrderForCaller(id, currentUserEmail(), staff);
        validateOrderIsModifiable(order, staff);

        Item item = findItem(id, itemId);
        item.updateQuantity(request.getQuantity());
        order.recalculateTotalPrice();
        return itemMapper.toResponseDto(itemRepository.save(item));
    }

    @Transactional
    public void deleteItem(UUID id, UUID itemId) {
        boolean staff = isStaff();
        Order order = resolveOrderForCaller(id, currentUserEmail(), staff);
        validateOrderIsModifiable(order, staff);
        Item item = findItem(id, itemId);
        itemRepository.delete(item);
        order.recalculateTotalPrice();
    }

    // Util methods

    private Order findOrder(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    private Item findItem(UUID id, UUID itemId) {
        return itemRepository.findByIdAndOrderId(itemId, id)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    private void validateOrderIsModifiable(Order order, boolean isStaff) {
        if (!isStaff && !order.getCurrentStatus().isModifiable()) {
            throw new OrderNotModifiableException(order.getId());
        }
    }

    private Order resolveOrderForCaller(UUID id, String callerEmail, boolean isStaff) {
        Optional<Order> order = isStaff
                ? orderRepository.findById(id)
                : orderRepository.findByIdAndUser_Email(id, callerEmail);
        return order.orElseThrow(() -> new OrderNotFoundException(id));
    }

    private String currentUserEmail() {
        return (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private boolean isStaff() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals(ORDER_READ_ALL));
    }
}