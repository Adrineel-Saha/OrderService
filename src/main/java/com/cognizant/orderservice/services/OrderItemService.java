package com.cognizant.orderservice.services;

import com.cognizant.orderservice.dtos.OrderItemDTO;

import java.util.List;

public interface OrderItemService {
    OrderItemDTO addItem(OrderItemDTO item);       // validate product & stock via Feign, set price, decrement stock
    List<OrderItemDTO> listItemsByOrder(Long orderId);
    OrderItemDTO updateItem(Long itemId, OrderItemDTO item);
    void deleteItem(Long itemId);
}