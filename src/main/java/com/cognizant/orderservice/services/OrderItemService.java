package com.cognizant.orderservice.services;

import com.cognizant.orderservice.dtos.OrderItemDTO;
import com.cognizant.orderservice.dtos.OrderItemResponseDTO;

import java.util.List;

public interface OrderItemService {
    OrderItemResponseDTO addItem(OrderItemDTO orderItemDTO);       // validate product & stock via Feign, set price, decrement stock
    OrderItemResponseDTO getItem(Long itemId);
    List<OrderItemResponseDTO> listItems();
    List<OrderItemResponseDTO> listItemsByProduct(Long productId);
    List<OrderItemResponseDTO> listItemsByOrder(Long orderId);
    OrderItemResponseDTO updateItem(Long itemId, OrderItemDTO orderItemDTO);
    String deleteItem(Long itemId);
}