package com.cognizant.orderservice.services;

import com.cognizant.orderservice.dtos.OrderDTO;
import com.cognizant.orderservice.dtos.OrderResponseDTO;
import com.cognizant.orderservice.dtos.UserDTO;
import com.cognizant.orderservice.entities.Order;
import com.cognizant.orderservice.exceptions.ResourceNotFoundException;
import com.cognizant.orderservice.feignclients.UserFeignClient;
import com.cognizant.orderservice.repositories.OrderRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private ModelMapper modelMapper;

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Override
    public OrderResponseDTO createOrder(OrderDTO orderDTO) {
        Long userId=orderDTO.getUserId();
        UserDTO userDTO=userFeignClient.getUser(userId);

        Order order=modelMapper.map(orderDTO,Order.class);
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder=orderRepository.save(order);

        OrderResponseDTO orderResponseDTO=modelMapper.map(savedOrder, OrderResponseDTO.class);
        modelMapper.map(userDTO,orderResponseDTO);
        return orderResponseDTO;
    }

    @Override
    public OrderResponseDTO getOrder(Long orderId) {
        Order order=orderRepository.findById(orderId).orElseThrow(
                ()->new ResourceNotFoundException("Order not found with Id: "+ orderId)
        );
        Long userId=order.getUserId();
        UserDTO userDTO=userFeignClient.getUser(userId);

        OrderResponseDTO orderResponseDTO=modelMapper.map(order, OrderResponseDTO.class);
        modelMapper.map(userDTO,orderResponseDTO);
        return orderResponseDTO;
    }

    @Override
    public List<OrderResponseDTO> listOrders() {
        List<Order> orderList=orderRepository.findAll();
        List<OrderResponseDTO> orderResponseDTOList=orderList.stream().
                map(order->{
                    Long userId=order.getUserId();
                    UserDTO userDTO=userFeignClient.getUser(userId);
                    OrderResponseDTO orderResponseDTO=modelMapper.map(order, OrderResponseDTO.class);
                    modelMapper.map(userDTO,orderResponseDTO);
                    return orderResponseDTO;
                }).toList();

        if(orderResponseDTOList.isEmpty()){
            throw new RuntimeException("Order List is Empty");
        }

        return orderResponseDTOList;
    }

    @Override
    public List<OrderResponseDTO> listOrdersByUser(Long userId) {
        UserDTO userDTO = userFeignClient.getUser(userId);

        List<Order> orderList = orderRepository.findByUserId(userId);

        List<OrderResponseDTO> orderResponseDTOList = orderList.stream().
                map(order -> {
                    OrderResponseDTO orderResponseDTO = modelMapper.map(order, OrderResponseDTO.class);
                    modelMapper.map(userDTO, orderResponseDTO);
                    return orderResponseDTO;
                }).toList();

        if(orderResponseDTOList.isEmpty()){
            throw new RuntimeException("Order List is Empty");
        }

        return orderResponseDTOList;
    }

    @Override
    public OrderResponseDTO updateOrderStatus(Long orderId, String status) {
        Order order=orderRepository.findById(orderId).orElseThrow(
                ()->new ResourceNotFoundException("Order not found with Id: "+ orderId)
        );

        order.setStatus(status);
        Order savedOrder=orderRepository.save(order);

        Long userId=savedOrder.getUserId();
        UserDTO userDTO=userFeignClient.getUser(userId);

        OrderResponseDTO orderResponseDTO=modelMapper.map(savedOrder, OrderResponseDTO.class);
        modelMapper.map(userDTO,orderResponseDTO);
        return orderResponseDTO;
    }

    @Override
    public String deleteOrder(Long orderId) {
        Order order=orderRepository.findById(orderId).orElseThrow(
                ()->new ResourceNotFoundException("Order not found with Id: "+ orderId)
        );

        log.info("Deleted Order: " + order);

        orderRepository.delete(order);
        return "Order deleted with Id: " + orderId;
    }
}
