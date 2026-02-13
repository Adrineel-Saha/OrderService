package com.cognizant.orderservice.services;

import com.cognizant.orderservice.dtos.OrderDTO;

import java.util.List;

public class OrderServiceImpl implements OrderService{
    @Override
    public OrderDTO createOrder(OrderDTO order) {
        return null;
    }

    @Override
    public OrderDTO getOrder(Long orderId) {
        return null;
    }

    @Override
    public List<OrderDTO> listOrders() {
        return List.of();
    }

    @Override
    public List<OrderDTO> listOrdersByUser(Long userId) {
        return List.of();
    }

    @Override
    public OrderDTO updateOrderStatus(Long orderId, String status) {
        return null;
    }

    @Override
    public void deleteOrder(Long orderId) {

    }
}
