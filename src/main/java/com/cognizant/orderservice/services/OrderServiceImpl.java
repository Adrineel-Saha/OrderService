package com.cognizant.orderservice.services;

import com.cognizant.orderservice.dtos.OrderDTO;
import com.cognizant.orderservice.dtos.OrderResponseDTO;
import com.cognizant.orderservice.dtos.UserDTO;
import com.cognizant.orderservice.entities.Order;
import com.cognizant.orderservice.exceptions.ResourceNotFoundException;
import com.cognizant.orderservice.feignclients.UserFeignClient;
import com.cognizant.orderservice.repositories.OrderRepository;
import org.modelmapper.ModelMapper;
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

    @Override
    public OrderResponseDTO createOrder(OrderDTO orderDTO) {
        Long userId=orderDTO.getUserId();
        UserDTO userDTO=userFeignClient.getUser(userId).orElseThrow(
                ()->new ResourceNotFoundException("User not found with Id: "+ userId)
        );

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
        UserDTO userDTO=userFeignClient.getUser(userId).orElseThrow(
                ()->new ResourceNotFoundException("User not found with Id: "+ userId)
        );

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
                    UserDTO userDTO=userFeignClient.getUser(userId).orElseThrow(
                            ()->new ResourceNotFoundException("User not found with Id: "+ userId)
                    );
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
        List<Order> orderList = orderRepository.findByUserId(userId);
        if (orderList.isEmpty()) {
            throw new RuntimeException("No Orders Found with User_Id: " + userId);
        }

        UserDTO userDTO = userFeignClient.getUser(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found with Id: " + userId)
        );
        List<OrderResponseDTO> orderResponseDTOList = orderList.stream().
                map(order -> {
                    OrderResponseDTO orderResponseDTO = modelMapper.map(order, OrderResponseDTO.class);
                    modelMapper.map(userDTO, orderResponseDTO);
                    return orderResponseDTO;
                }).toList();

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
        UserDTO userDTO=userFeignClient.getUser(userId).orElseThrow(
                ()->new ResourceNotFoundException("User not found with Id: "+ userId)
        );

        OrderResponseDTO orderResponseDTO=modelMapper.map(savedOrder, OrderResponseDTO.class);
        modelMapper.map(userDTO,orderResponseDTO);
        return orderResponseDTO;
    }

    @Override
    public String deleteOrder(Long orderId) {
        Order order=orderRepository.findById(orderId).orElseThrow(
                ()->new ResourceNotFoundException("Order not found with Id: "+ orderId)
        );

//        log.info("Deleted Product: " + product);

        orderRepository.delete(order);
        return "Order deleted with Id: " + orderId;
    }
}
