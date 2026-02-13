package com.cognizant.orderservice.services;

import com.cognizant.orderservice.dtos.OrderDTO;

import java.util.List;

public interface OrderService {
    OrderDTO createOrder(OrderDTO order);          // validate user via Feign
    OrderDTO getOrder(Long orderId);
    List<OrderDTO> listOrders();
    List<OrderDTO> listOrdersByUser(Long userId);
    OrderDTO updateOrderStatus(Long orderId, String status);
    void deleteOrder(Long orderId);                // relies on DB FK for items
}