package com.santiGalarza.order_management.order;

import com.santiGalarza.order_management.order.item.*;
import com.santiGalarza.order_management.order.status.OrderStatusService;
import com.santiGalarza.order_management.order.status.UpdateStatusRequest;
import com.santiGalarza.order_management.product.Product;
import com.santiGalarza.order_management.product.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderMapper orderMapper;
    private final ItemMapper itemMapper;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final ProductService productService;
    private final OrderStatusService orderStatusService;

    @Value("${order.delivery.max-attempts}")
    private int maxDeliveryAttempts;

    public OrderService(
            OrderMapper orderMapper, OrderRepository orderRepository,
            ProductService productService, ItemRepository itemRepository, ItemMapper itemMapper, OrderStatusService orderStatusService) {
        this.orderMapper = orderMapper;
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.productService = productService;
        this.orderStatusService = orderStatusService;
    }

    // Order Class service methods

    public List<OrderResponse> getOrders(){
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrder(UUID id){
        Order order = findOrder(id);

        return orderMapper.toResponseDto(order);
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request){
        List<UUID> productIds = request.getItems()
                .stream()
                .map(CreateItemRequest::getProductId)
                .distinct()
                .toList();

        Map<UUID, Product> productMap = productService.getProductsByIds(productIds);

        for(CreateItemRequest item : request.getItems()){
            Product product = productMap.get(item.getProductId());
            product.deductStock(item.getQuantity());
        }

        Order order = orderMapper.toEntity(request);
        order.setCurrentStatus(orderStatusService.getInitialStatus());
        return orderMapper.toResponseDto(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse updateStatus(UUID id, UpdateStatusRequest request){
        Order order = findOrder(id);
        // changedBy will be replaced with actual auth principal once auth is in
        orderStatusService.transition(order, request.getStatusCode(), null, request.getNotes());

        return orderMapper.toResponseDto(orderRepository.save(order));
    }

    @Transactional
    public void deleteOrder(UUID id){
        findOrder(id);
        orderRepository.deleteById(id);
    }

    // Item Class service methods

    public List<ItemResponse> getItems(UUID id){
        return findOrder(id).getItems()
                .stream()
                .map(itemMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public ItemResponse getItem(UUID id, UUID itemId){
        return itemMapper.toResponseDto(findItem(id,itemId));
    }

    @Transactional
    public ItemResponse createItem(UUID id, CreateItemRequest request){
        Order order = findOrder(id);
        validateOrderIsModifiable(order);

        Product product = productService.findProduct(request.getProductId());
        product.deductStock(request.getQuantity());

        Item item = itemMapper.toEntity(request);
        item.setOrder(order);
        item.setProduct(product);
        order.getItems().add(item);

        return itemMapper.toResponseDto(itemRepository.save(item));
    }

    @Transactional
    public ItemResponse updateItemQuantity(UUID id, UUID itemId, PatchItemRequest request){
        validateOrderIsModifiable(findOrder(id));

        Item item = findItem(id,itemId);
        item.updateQuantity(request.getQuantity());
        return itemMapper.toResponseDto(itemRepository.save(item));
    }

    @Transactional
    public void deleteItem(UUID id, UUID itemId){
        validateOrderIsModifiable(findOrder(id));
        Item item = findItem(id,itemId);
        itemRepository.delete(item);
    }

    // Util methods

    private Order findOrder(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    private Item findItem(UUID id,UUID itemId) {
        return itemRepository.findByIdAndOrderId(itemId,id)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    private void validateOrderIsModifiable(Order order) {
        if (!order.getCurrentStatus().isModifiable()) {
            throw new OrderNotModifiableException(order.getId());
        }
    }
}