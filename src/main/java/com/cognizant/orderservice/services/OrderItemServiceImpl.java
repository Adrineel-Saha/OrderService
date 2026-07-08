package com.cognizant.orderservice.services;

import com.cognizant.orderservice.dtos.OrderItemDTO;
import com.cognizant.orderservice.dtos.OrderItemResponseDTO;
import com.cognizant.orderservice.dtos.ProductDTO;
import com.cognizant.orderservice.entities.Order;
import com.cognizant.orderservice.entities.OrderItem;
import com.cognizant.orderservice.exceptions.ResourceNotFoundException;
import com.cognizant.orderservice.feignclients.ProductFeignClient;
import com.cognizant.orderservice.repositories.OrderItemRepository;
import com.cognizant.orderservice.repositories.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
@Transactional(readOnly = true)
public class OrderItemServiceImpl implements OrderItemService{
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private Map<Long, ProductDTO> productCache;

//    @KafkaListener(topics = "${app.kafka.productproducer.topic}", groupId = "orderServiceGroup")
//    public void consumeProductEvent(ProductDTO productDTO) {
//        productCache.put(productDTO.getId(), productDTO);
//        log.info("Received and cached ProductDTO from product-events: {}", productDTO);
//    }

    @CircuitBreaker(name = "OrderMicroservice", fallbackMethod = "addItemGetDefaultProduct")
    @Transactional
    @Override
    public OrderItemResponseDTO addItem(OrderItemDTO orderItemDTO) {
        findOrderOrThrow(orderItemDTO.getOrderId());
        ProductDTO productDTO = productFeignClient.getProduct(orderItemDTO.getProductId());
        return saveItemAndBuildResponse(orderItemDTO, productDTO);
    }

    public OrderItemResponseDTO addItemGetDefaultProduct(OrderItemDTO orderItemDTO , Throwable throwable) {
        findOrderOrThrow(orderItemDTO.getOrderId());
        ProductDTO productDTO = productCache.getOrDefault(orderItemDTO.getProductId(), getFallbackProduct(orderItemDTO.getProductId()));
        return saveItemAndBuildResponse(orderItemDTO, productDTO);
    }

    private OrderItemResponseDTO saveItemAndBuildResponse(OrderItemDTO orderItemDTO, ProductDTO productDTO) {
        OrderItem orderItem = modelMapper.map(orderItemDTO, OrderItem.class);
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        OrderItemResponseDTO orderItemResponseDTO = toOrderItemResponseDTO(savedOrderItem, productDTO);
        orderItemResponseDTO.setOrderId(savedOrderItem.getId());
        return orderItemResponseDTO;
    }

    @CircuitBreaker(name = "OrderMicroservice", fallbackMethod = "getItemGetDefaultProduct")
    @Override
    public OrderItemResponseDTO getItem(Long itemId) {
        OrderItem orderItem = findItemOrThrow(itemId);
        ProductDTO productDTO = productFeignClient.getProduct(orderItem.getProductId());
        return toOrderItemResponseWithOrderId(orderItem, productDTO);
    }

    public OrderItemResponseDTO getItemGetDefaultProduct(Long itemId , Throwable throwable) {
        OrderItem orderItem = findItemOrThrow(itemId);
        ProductDTO productDTO = productCache.getOrDefault(orderItem.getProductId(), getFallbackProduct(orderItem.getProductId()));
        return toOrderItemResponseWithOrderId(orderItem, productDTO);
    }

    @CircuitBreaker(name = "OrderMicroservice", fallbackMethod = "listItemsGetDefaultProduct")
    @Override
    public List<OrderItemResponseDTO> listItems() {
        List<OrderItem> orderItemList = orderItemRepository.findAll();
        return toOrderItemResponseList(orderItemList, item -> productFeignClient.getProduct(item.getProductId()));
    }

    public List<OrderItemResponseDTO> listItemsGetDefaultProduct(Throwable throwable) {
        List<OrderItem> orderItemList = orderItemRepository.findAll();
        return toOrderItemResponseList(orderItemList, item -> productCache.getOrDefault(item.getProductId(), getFallbackProduct(item.getProductId())));
    }

    @CircuitBreaker(name = "OrderMicroservice", fallbackMethod = "listItemsByProductGetDefaultProduct")
    @Override
    public List<OrderItemResponseDTO> listItemsByProduct(Long productId) {
        ProductDTO productDTO = productFeignClient.getProduct(productId);
        List<OrderItem> orderItemList = orderItemRepository.findByProductId(productId);
        return toOrderItemResponseList(orderItemList, item -> productDTO);
    }

    public List<OrderItemResponseDTO> listItemsByProductGetDefaultProduct(Long productId , Throwable throwable) {
        ProductDTO productDTO = productCache.getOrDefault(productId, getFallbackProduct(productId));
        List<OrderItem> orderItemList = orderItemRepository.findByProductId(productId);
        return toOrderItemResponseList(orderItemList, item -> productDTO);
    }

    @CircuitBreaker(name = "OrderMicroservice", fallbackMethod = "listItemsByOrderGetDefaultProduct")
    @Override
    public List<OrderItemResponseDTO> listItemsByOrder(Long orderId) {
        List<OrderItem> orderItemList = orderItemRepository.findByOrderId(orderId);
        return toOrderItemResponseList(orderItemList, item -> productFeignClient.getProduct(item.getProductId()), orderId);
    }

    public List<OrderItemResponseDTO> listItemsByOrderGetDefaultProduct(Long orderId , Throwable throwable) {
        List<OrderItem> orderItemList = orderItemRepository.findByOrderId(orderId);
        return toOrderItemResponseList(orderItemList, item -> productCache.getOrDefault(item.getProductId(), getFallbackProduct(item.getProductId())), orderId);
    }

    @CircuitBreaker(name = "OrderMicroservice", fallbackMethod = "updateItemGetDefaultProduct")
    @Transactional
    @Override
    public OrderItemResponseDTO updateItem(Long itemId, OrderItemDTO orderItemDTO) {
        OrderItem savedOrderItem = applyItemUpdateAndSave(itemId, orderItemDTO);
        ProductDTO productDTO = productFeignClient.getProduct(savedOrderItem.getProductId());
        return toOrderItemResponseWithOrderId(savedOrderItem, productDTO, orderItemDTO.getOrderId());
    }

    public OrderItemResponseDTO updateItemGetDefaultProduct(Long itemId, OrderItemDTO orderItemDTO , Throwable throwable) {
        OrderItem savedOrderItem = applyItemUpdateAndSave(itemId, orderItemDTO);
        ProductDTO productDTO = productCache.getOrDefault(savedOrderItem.getProductId(), getFallbackProduct(savedOrderItem.getProductId()));
        return toOrderItemResponseWithOrderId(savedOrderItem, productDTO, orderItemDTO.getOrderId());
    }

    private OrderItem applyItemUpdateAndSave(Long itemId, OrderItemDTO orderItemDTO) {
        orderItemDTO.setId(itemId);
        Long orderId = orderItemDTO.getOrderId();

        Order order = findOrderOrThrow(orderId);
        OrderItem orderItem = orderItemRepository.findById(itemId).orElseThrow(
                () -> new ResourceNotFoundException("Order Item not found with Id: " + itemId)
        );

        if (!orderId.equals(orderItem.getOrder().getId())) {
            throw new ResourceNotFoundException("Please enter matching Order Id: " + orderItem.getOrder().getId());
        }

        modelMapper.map(orderItemDTO, orderItem);
        orderItem.setOrder(order);

        return orderItemRepository.save(orderItem);
    }

    @Transactional
    @Override
    public String deleteItem(Long itemId) {
        OrderItem orderItem = findItemOrThrow(itemId);

        log.info("Deleted Item: {}", orderItem);

        orderItemRepository.delete(orderItem);
        return "Item deleted with Id: " + itemId;
    }

    private Order findOrderOrThrow(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException("Order not found with Id: " + orderId)
        );
    }

    private OrderItem findItemOrThrow(Long itemId) {
        return orderItemRepository.findById(itemId).orElseThrow(
                () -> new ResourceNotFoundException("Item not found with Id: " + itemId)
        );
    }

    private List<OrderItemResponseDTO> toOrderItemResponseList(List<OrderItem> orderItemList, Function<OrderItem, ProductDTO> productResolver) {
        List<OrderItemResponseDTO> orderItemResponseDTOList = orderItemList.stream()
                .map(orderItem -> toOrderItemResponseWithOrderId(orderItem, productResolver.apply(orderItem)))
                .toList();

        if (orderItemResponseDTOList.isEmpty()) {
            throw new RuntimeException("Item List is Empty");
        }

        return orderItemResponseDTOList;
    }

    private List<OrderItemResponseDTO> toOrderItemResponseList(List<OrderItem> orderItemList, Function<OrderItem, ProductDTO> productResolver, Long orderId) {
        List<OrderItemResponseDTO> orderItemResponseDTOList = orderItemList.stream()
                .map(orderItem -> toOrderItemResponseWithOrderId(orderItem, productResolver.apply(orderItem), orderId))
                .toList();

        if (orderItemResponseDTOList.isEmpty()) {
            throw new RuntimeException("Item List is Empty");
        }

        return orderItemResponseDTOList;
    }

    private OrderItemResponseDTO toOrderItemResponseWithOrderId(OrderItem orderItem, ProductDTO productDTO) {
        return toOrderItemResponseWithOrderId(orderItem, productDTO, orderItem.getOrder().getId());
    }

    private OrderItemResponseDTO toOrderItemResponseWithOrderId(OrderItem orderItem, ProductDTO productDTO, Long orderId) {
        OrderItemResponseDTO orderItemResponseDTO = toOrderItemResponseDTO(orderItem, productDTO);
        orderItemResponseDTO.setOrderId(orderId);
        return orderItemResponseDTO;
    }

    private OrderItemResponseDTO toOrderItemResponseDTO(OrderItem orderItem, ProductDTO productDTO) {
        OrderItemResponseDTO orderItemResponseDTO = modelMapper.map(orderItem, OrderItemResponseDTO.class);
        modelMapper.map(productDTO, orderItemResponseDTO);
        return orderItemResponseDTO;
    }

    private ProductDTO getFallbackProduct(Long productId) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(productId);
        productDTO.setName("Unknown Product");
        productDTO.setDescription("Product details unavailable");
        productDTO.setPrice(0.0);
        productDTO.setStock(0);
        return productDTO;
    }
}
