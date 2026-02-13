package com.cognizant.orderservice.services;

import com.cognizant.orderservice.dtos.OrderItemDTO;

import java.util.List;

public class OrderItemServiceImpl implements OrderItemService{
    @Override
    public OrderItemDTO addItem(OrderItemDTO item) {
        return null;
    }

    @Override
    public List<OrderItemDTO> listItemsByOrder(Long orderId) {
        return List.of();
    }

    @Override
    public OrderItemDTO updateItem(Long itemId, OrderItemDTO item) {
        return null;
    }

    @Override
    public void deleteItem(Long itemId) {

    }
}
