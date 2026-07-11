package com.santiGalarza.order_management.order;

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

    @Value("${order.delivery.max-attempts}")
    private int maxDeliveryAttempts;

    public OrderService(
            OrderMapper orderMapper, OrderRepository orderRepository,
            ProductService productService, ItemRepository itemRepository, ItemMapper itemMapper) {
        this.orderMapper = orderMapper;
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.productService = productService;
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
    public OrderResponse createOrder(CreateOrderRequest createOrderRequest){
        List<UUID> productIds = createOrderRequest.getItems()
                .stream()
                .map(CreateItemRequest::getProductId)
                .distinct()
                .toList();

        Map<UUID, Product> productMap = productService.getProductsByIds(productIds);

        for(CreateItemRequest item : createOrderRequest.getItems()){
            Product product = productMap.get(item.getProductId());
            product.deductStock(item.getQuantity());
        }

        Order order = orderMapper.toEntity(createOrderRequest);
        return orderMapper.toResponseDto(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse updateStatus(UUID id, UpdateStatusRequest updateStatusRequest){
        Order order = findOrder(id);
        order.updateStatus(updateStatusRequest.getStatus(),maxDeliveryAttempts);
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
    public ItemResponse createItem(UUID id, CreateItemRequest createItemRequest){
        Order order = findOrder(id);
        validateOrderIsModifiable(order);

        Product product = productService.findProduct(createItemRequest.getProductId());
        product.deductStock(createItemRequest.getQuantity());

        Item item = itemMapper.toEntity(createItemRequest);
        item.setOrder(order);
        item.setProduct(product);
        order.getItems().add(item);

        return itemMapper.toResponseDto(itemRepository.save(item));
    }

    @Transactional
    public ItemResponse updateItemQuantity(UUID id, UUID itemId, PatchItemRequest patchItemRequest){
        validateOrderIsModifiable(findOrder(id));

        Item item = findItem(id,itemId);
        item.updateQuantity(patchItemRequest.getQuantity());
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

    private void validateOrderIsModifiable(Order order){
        if (!order.getStatus().isModifiable()) {
            throw new OrderNotModifiableException(order.getId());
        }
    }
}