package com.cognizant.orderservice.services;

import com.cognizant.orderservice.dtos.OrderDTO;
import com.cognizant.orderservice.dtos.OrderResponseDTO;
import com.cognizant.orderservice.dtos.UserDTO;
import com.cognizant.orderservice.entities.Order;
import com.cognizant.orderservice.exceptions.ResourceNotFoundException;
import com.cognizant.orderservice.feignclients.UserFeignClient;
import com.cognizant.orderservice.repositories.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService{
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private Map<Long, UserDTO> userCache;

    @KafkaListener(topics = "${app.kafka.userproducer.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserEvent(UserDTO userDTO) {
        userCache.put(userDTO.getId(), userDTO);
        log.info("Received and cached UserDTO from user-events: {}", userDTO);
    }

    @CircuitBreaker(name = "OrderMicroservice", fallbackMethod = "createOrderGetDefaultUser")
    @Transactional
    @Override
    public OrderResponseDTO createOrder(OrderDTO orderDTO) {
        UserDTO userDTO = userFeignClient.getUser(orderDTO.getUserId());
        return saveOrderAndBuildResponse(orderDTO, userDTO);
    }

    public OrderResponseDTO createOrderGetDefaultUser(OrderDTO orderDTO , Throwable throwable) {
        UserDTO userDTO = userCache.getOrDefault(orderDTO.getUserId(), getFallbackUser(orderDTO.getUserId()));
        return saveOrderAndBuildResponse(orderDTO, userDTO);
    }

    private OrderResponseDTO saveOrderAndBuildResponse(OrderDTO orderDTO, UserDTO userDTO) {
        Order order = modelMapper.map(orderDTO, Order.class);
        order.setCreatedAt(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);
        return toOrderResponseDTO(savedOrder, userDTO);
    }

    @CircuitBreaker(name = "OrderMicroservice", fallbackMethod = "getOrderGetDefaultUser")
    @Override
    public OrderResponseDTO getOrder(Long orderId) {
        Order order = findOrderOrThrow(orderId);
        UserDTO userDTO = userFeignClient.getUser(order.getUserId());
        return toOrderResponseDTO(order, userDTO);
    }

    public OrderResponseDTO getOrderGetDefaultUser(Long orderId , Throwable throwable) {
        Order order = findOrderOrThrow(orderId);
        UserDTO userDTO = userCache.getOrDefault(order.getUserId(), getFallbackUser(order.getUserId()));
        return toOrderResponseDTO(order, userDTO);
    }

    @CircuitBreaker(name = "OrderMicroservice", fallbackMethod = "listOrdersGetDefaultUser")
    @Override
    public List<OrderResponseDTO> listOrders() {
        List<Order> orderList = orderRepository.findAll();
        return toOrderResponseList(orderList, order -> userFeignClient.getUser(order.getUserId()));
    }

    public List<OrderResponseDTO> listOrdersGetDefaultUser(Throwable throwable) {
        List<Order> orderList = orderRepository.findAll();
        return toOrderResponseList(orderList, order -> userCache.getOrDefault(order.getUserId(), getFallbackUser(order.getUserId())));
    }

    @CircuitBreaker(name = "OrderMicroservice", fallbackMethod = "listOrdersByUserGetDefaultUser")
    @Override
    public List<OrderResponseDTO> listOrdersByUser(Long userId) {
        UserDTO userDTO = userFeignClient.getUser(userId);
        List<Order> orderList = orderRepository.findByUserId(userId);
        return toOrderResponseList(orderList, order -> userDTO);
    }

    public List<OrderResponseDTO> listOrdersByUserGetDefaultUser(Long userId , Throwable throwable) {
        UserDTO userDTO = userCache.getOrDefault(userId, getFallbackUser(userId));
        List<Order> orderList = orderRepository.findByUserId(userId);
        return toOrderResponseList(orderList, order -> userDTO);
    }

    @CircuitBreaker(name = "OrderMicroservice", fallbackMethod = "updateOrderStatusGetDefaultUser")
    @Transactional
    @Override
    public OrderResponseDTO updateOrderStatus(Long orderId, String status) {
        Order savedOrder = updateStatusAndSave(orderId, status);
        UserDTO userDTO = userFeignClient.getUser(savedOrder.getUserId());
        return toOrderResponseDTO(savedOrder, userDTO);
    }

    public OrderResponseDTO updateOrderStatusGetDefaultUser(Long orderId, String status , Throwable throwable) {
        Order savedOrder = updateStatusAndSave(orderId, status);
        UserDTO userDTO = userCache.getOrDefault(savedOrder.getUserId(), getFallbackUser(savedOrder.getUserId()));
        return toOrderResponseDTO(savedOrder, userDTO);
    }

    private Order updateStatusAndSave(Long orderId, String status) {
        Order order = findOrderOrThrow(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Transactional
    @Override
    public String deleteOrder(Long orderId) {
        Order order = findOrderOrThrow(orderId);

        log.info("Deleted Order: {}", order);

        orderRepository.delete(order);
        return "Order deleted with Id: " + orderId;
    }

    private Order findOrderOrThrow(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException("Order not found with Id: " + orderId)
        );
    }

    private List<OrderResponseDTO> toOrderResponseList(List<Order> orderList, Function<Order, UserDTO> userResolver) {
        List<OrderResponseDTO> orderResponseDTOList = orderList.stream()
                .map(order -> toOrderResponseDTO(order, userResolver.apply(order)))
                .toList();

        if (orderResponseDTOList.isEmpty()) {
            throw new RuntimeException("Order List is Empty");
        }

        return orderResponseDTOList;
    }

    private OrderResponseDTO toOrderResponseDTO(Order order, UserDTO userDTO) {
        OrderResponseDTO orderResponseDTO = modelMapper.map(order, OrderResponseDTO.class);
        modelMapper.map(userDTO, orderResponseDTO);
        return orderResponseDTO;
    }

    private UserDTO getFallbackUser(Long userId) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId);
        userDTO.setUserName("Unknown");
        userDTO.setEmail("unknown@example.com");
        return userDTO;
    }
}
