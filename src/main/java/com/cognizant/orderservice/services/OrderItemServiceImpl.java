package com.cognizant.orderservice.services;

import com.cognizant.orderservice.dtos.OrderItemDTO;
import com.cognizant.orderservice.dtos.OrderItemResponseDTO;
import com.cognizant.orderservice.dtos.OrderResponseDTO;
import com.cognizant.orderservice.dtos.ProductDTO;
import com.cognizant.orderservice.entities.Order;
import com.cognizant.orderservice.entities.OrderItem;
import com.cognizant.orderservice.exceptions.ResourceNotFoundException;
import com.cognizant.orderservice.feignclients.ProductFeignClient;
import com.cognizant.orderservice.feignclients.UserFeignClient;
import com.cognizant.orderservice.repositories.OrderItemRepository;
import com.cognizant.orderservice.repositories.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class OrderItemServiceImpl implements OrderItemService{
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public OrderItemResponseDTO addItem(OrderItemDTO orderItemDTO) {
        Long orderId=orderItemDTO.getOrderId();
        Order order=orderRepository.findById(orderId).orElseThrow(
                ()->new RuntimeException("Order not found with Id: "+ orderId)
        );

        Long productId=orderItemDTO.getProductId();
        ProductDTO productDTO=productFeignClient.getProduct(productId).orElseThrow(
                ()->new RuntimeException("Product not found with Id: "+ productId)
        );

        OrderItem orderItem=modelMapper.map(orderItemDTO,OrderItem.class);
        OrderItem savedOrderItem=orderItemRepository.save(orderItem);

        OrderItemResponseDTO orderItemResponseDTO=modelMapper.map(savedOrderItem,OrderItemResponseDTO.class);
        modelMapper.map(productDTO,orderItemResponseDTO);
        orderItemResponseDTO.setOrderId(savedOrderItem.getId());
        return orderItemResponseDTO;
    }

    @Override
    public List<OrderItemResponseDTO> listItemsByOrder(Long orderId) {
        List<OrderItem> orderItemList=orderItemRepository.findByOrderId(orderId);
        List<OrderItemResponseDTO> orderItemResponseDTOList=orderItemList.stream().map(
                orderItem->{
                    Long productId=orderItem.getProductId();
                    ProductDTO productDTO=productFeignClient.getProduct(productId).orElseThrow(
                            ()->new RuntimeException("Product not found with Id: "+ productId)
                    );
                    OrderItemResponseDTO orderItemResponseDTO=modelMapper.map(orderItem,OrderItemResponseDTO.class);
                    modelMapper.map(productDTO,orderItemResponseDTO);
                    orderItemResponseDTO.setOrderId(orderId);
                    return orderItemResponseDTO;
                }
        ).toList();

        if(orderItemResponseDTOList.isEmpty()){
            throw new RuntimeException("Order Item List is Empty");
        }

        return orderItemResponseDTOList;
    }

    @Override
    public OrderItemResponseDTO updateItem(Long itemId, OrderItemDTO orderItemDTO) {
        Long orderId=orderItemDTO.getOrderId();

        Order order=orderRepository.findById(orderItemDTO.getOrderId()).orElseThrow(
                ()->new RuntimeException("Order not found with Id: "+ orderId)
        );

        OrderItem orderItem=orderItemRepository.findById(itemId).orElseThrow(
                ()->new RuntimeException("Order Item not found with Id: "+ itemId)
        );

        modelMapper.map(orderItemDTO,orderItem);
        orderItem.setOrder(order);
        OrderItem savedOrderItem=orderItemRepository.save(orderItem);

        Long productId= savedOrderItem.getProductId();
        ProductDTO productDTO=productFeignClient.getProduct(productId).orElseThrow(
                ()->new RuntimeException("Product not found with Id: "+ productId)
        );

        OrderItemResponseDTO orderItemResponseDTO=modelMapper.map(savedOrderItem, OrderItemResponseDTO.class);
        modelMapper.map(productDTO,orderItemResponseDTO);
        orderItemResponseDTO.setOrderId(orderId);
        return orderItemResponseDTO;
    }

    @Override
    public String deleteItem(Long itemId) {
        OrderItem orderItem=orderItemRepository.findById(itemId).orElseThrow(
                ()->new ResourceNotFoundException("Order Item  not found with Id: "+ itemId)
        );

//        log.info("Deleted Product: " + product);

        orderItemRepository.delete(orderItem);
        return "Order Item deleted with Id: " + itemId;
    }
}
