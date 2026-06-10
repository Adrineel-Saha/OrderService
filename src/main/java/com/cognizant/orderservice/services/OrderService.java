package com.cognizant.orderservice.services;

import com.cognizant.orderservice.dtos.OrderDTO;
import com.cognizant.orderservice.dtos.OrderResponseDTO;

import java.util.List;

public interface OrderService {
    OrderResponseDTO createOrder(OrderDTO orderDTO);
    OrderResponseDTO getOrder(Long orderId);
    List<OrderResponseDTO> listOrders();
    List<OrderResponseDTO> listOrdersByUser(Long userId);
    OrderResponseDTO updateOrderStatus(Long orderId, String status);
    String deleteOrder(Long orderId);
}