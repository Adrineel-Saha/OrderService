package com.cognizant.orderservice.test.services;

import com.cognizant.orderservice.dtos.OrderDTO;
import com.cognizant.orderservice.dtos.OrderResponseDTO;
import com.cognizant.orderservice.dtos.UserDTO;
import com.cognizant.orderservice.entities.Order;
import com.cognizant.orderservice.exceptions.ResourceNotFoundException;
import com.cognizant.orderservice.feignclients.UserFeignClient;
import com.cognizant.orderservice.repositories.OrderRepository;
import com.cognizant.orderservice.services.OrderServiceImpl;
import feign.FeignException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestOrderServiceImpl {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserFeignClient userFeignClient;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private OrderServiceImpl orderServiceImpl;

    private Validator validator;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    public void testListOrdersPositiveOneOrderFound(){
        try{
            List<Order> orderListMock=mock(List.class);
            when(orderRepository.findAll()).thenReturn(orderListMock);

            Order orderMock=mock(Order.class);
            when(orderListMock.stream()).thenReturn(Stream.of(orderMock));

            when(orderMock.getUserId()).thenReturn(1L);

            UserDTO userDTOMock=mock(UserDTO.class);
            when(userFeignClient.getUser(any())).thenReturn(userDTOMock);

            OrderResponseDTO orderResponseDTOMock=mock(OrderResponseDTO.class);
            when(modelMapper.map(any(Order.class),eq(OrderResponseDTO.class))).thenReturn(orderResponseDTOMock);
            when(modelMapper.map(any(UserDTO.class),eq(OrderResponseDTO.class))).thenReturn(orderResponseDTOMock);

            List<OrderResponseDTO> orderResponseDTOList=orderServiceImpl.listOrders();
            assertTrue(orderResponseDTOList.size()==1);
        }catch(Exception ex){
            assertTrue(false);
        }
    }

    @Test
    public void testListOrdersPositiveMultipleOrdersFound(){
        try{
            List<Order> orderListMock=mock(List.class);
            when(orderRepository.findAll()).thenReturn(orderListMock);

            Order orderMock1=mock(Order.class);
            Order orderMock2=mock(Order.class);
            when(orderListMock.stream()).thenReturn(Stream.of(orderMock1,orderMock2));

            when(orderMock1.getUserId()).thenReturn(1L);
            when(orderMock2.getUserId()).thenReturn(2L);

            UserDTO userDTOMock1=mock(UserDTO.class);
            UserDTO userDTOMock2=mock(UserDTO.class);
            when(userFeignClient.getUser(any())).thenReturn(userDTOMock1).thenReturn(userDTOMock2);

            OrderResponseDTO orderResponseDTOMock1=mock(OrderResponseDTO.class);
            OrderResponseDTO orderResponseDTOMock2=mock(OrderResponseDTO.class);
            when(modelMapper.map(any(Order.class),eq(OrderResponseDTO.class))).thenReturn(orderResponseDTOMock1).thenReturn(orderResponseDTOMock2);
            when(modelMapper.map(any(UserDTO.class),eq(OrderResponseDTO.class))).thenReturn(orderResponseDTOMock1).thenReturn(orderResponseDTOMock2);

            List<OrderResponseDTO> orderResponseDTOList=orderServiceImpl.listOrders();
            assertTrue(orderResponseDTOList.size()>1);
        }catch(Exception ex){
            assertTrue(false);
        }
    }

    @Test
    public void testListOrdersNegativeWhenUserIsNotFound(){
        try{
            Order orderMock=mock(Order.class);
            when(orderRepository.findAll()).thenReturn(List.of(orderMock));

            when(orderMock.getUserId()).thenReturn(1L);

            FeignException notFound = mock(FeignException.NotFound.class);
            when(notFound.getMessage()).thenReturn("User not found with Id: 1");
            when(notFound.status()).thenReturn(404);

            when(userFeignClient.getUser(any())).thenThrow(notFound);

            List<OrderResponseDTO> orderResponseDTOList=orderServiceImpl.listOrders();
        } catch(Exception ex){
            assertThatThrownBy(() -> orderServiceImpl.listOrders())
                    .isInstanceOf(FeignException.NotFound.class)
                    .hasMessage("User not found with Id: 1");
        }
    }

    @Test
    public void testListOrdersNegativeWhenListIsEmpty(){
        try{
            when(orderRepository.findAll()).thenReturn(List.of());

            List<OrderResponseDTO> orderResponseDTOList=orderServiceImpl.listOrders();

        } catch(Exception ex){
            assertThatThrownBy(() -> orderServiceImpl.listOrders())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Order List is Empty");
        }
    }

    @Test
    public void testListOrdersByUsersPositiveOneOrderFound(){
        try{
            Long userId=1l;

            UserDTO userDTOMock=mock(UserDTO.class);
            when(userFeignClient.getUser(any())).thenReturn(userDTOMock);

            List<Order> orderListMock=mock(List.class);
            when(orderRepository.findByUserId(any())).thenReturn(orderListMock);

            Order orderMock=mock(Order.class);
            when(orderListMock.stream()).thenReturn(Stream.of(orderMock));

            OrderResponseDTO orderResponseDTOMock=mock(OrderResponseDTO.class);
            when(modelMapper.map(any(Order.class),eq(OrderResponseDTO.class))).thenReturn(orderResponseDTOMock);
            when(modelMapper.map(any(UserDTO.class),eq(OrderResponseDTO.class))).thenReturn(orderResponseDTOMock);

            List<OrderResponseDTO> orderResponseDTOList=orderServiceImpl.listOrdersByUser(userId);
            assertTrue(orderResponseDTOList.size()==1);
        }catch(Exception ex){
            assertTrue(false);
        }
    }

    @Test
    public void testListOrdersByUserPositiveMultipleOrdersFound(){
        try{
            Long userId=1l;

            UserDTO userDTOMock=mock(UserDTO.class);
            when(userFeignClient.getUser(any())).thenReturn(userDTOMock);

            List<Order> orderListMock=mock(List.class);
            when(orderRepository.findByUserId(any())).thenReturn(orderListMock);

            Order orderMock1=mock(Order.class);
            Order orderMock2=mock(Order.class);
            when(orderListMock.stream()).thenReturn(Stream.of(orderMock1,orderMock2));

            OrderResponseDTO orderResponseDTOMock1=mock(OrderResponseDTO.class);
            OrderResponseDTO orderResponseDTOMock2=mock(OrderResponseDTO.class);
            when(modelMapper.map(any(Order.class),eq(OrderResponseDTO.class))).thenReturn(orderResponseDTOMock1).thenReturn(orderResponseDTOMock2);
            when(modelMapper.map(any(UserDTO.class),eq(OrderResponseDTO.class))).thenReturn(orderResponseDTOMock1).thenReturn(orderResponseDTOMock2);

            List<OrderResponseDTO> orderResponseDTOList=orderServiceImpl.listOrdersByUser(userId);
            assertTrue(orderResponseDTOList.size()>1);
        }catch(Exception ex){
            assertTrue(false);
        }
    }

    @Test
    public void testListOrdersByUserNegativeWhenUserIsNotFound(){
        Long userId=1l;
        try{

            FeignException notFound = mock(FeignException.NotFound.class);
            when(notFound.getMessage()).thenReturn("User not found with Id: 1");
            when(notFound.status()).thenReturn(404);

            when(userFeignClient.getUser(any())).thenThrow(notFound);

            List<OrderResponseDTO> orderResponseDTOList=orderServiceImpl.listOrdersByUser(userId);

        } catch(Exception ex){
            assertThatThrownBy(() -> orderServiceImpl.listOrdersByUser(userId))
                    .isInstanceOf(FeignException.NotFound.class)
                    .hasMessage("User not found with Id: 1");
        }
    }

    @Test
    public void testListOrdersByUserNegativeWhenListIsEmpty(){
        Long userId=1l;
        try{
            when(orderRepository.findByUserId(any())).thenReturn(List.of());

            List<OrderResponseDTO> orderResponseDTOList=orderServiceImpl.listOrdersByUser(userId);

        } catch(Exception ex){
            assertThatThrownBy(() -> orderServiceImpl.listOrdersByUser(userId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Order List is Empty");
        }
    }

    @Test
    public void testGetOrderPositive(){
        try{
            Order order=new Order();
            order.setId(1L);
            order.setUserId(1L);
            order.setStatus("CREATED");
            order.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));

            UserDTO userDTO=new UserDTO();
            userDTO.setId(1L);
            userDTO.setUserName("Aman");
            userDTO.setEmail("Aman@example.com");

            OrderResponseDTO orderResponseDTO=new OrderResponseDTO();
            orderResponseDTO.setId(1L);
            orderResponseDTO.setUserId(1L);
            orderResponseDTO.setStatus("CREATED");
            orderResponseDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));
            orderResponseDTO.setUserName("Aman");
            orderResponseDTO.setEmail("Aman@example.com");

            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            when(userFeignClient.getUser(any())).thenReturn(userDTO);

            when(modelMapper.map(any(Order.class),eq(OrderResponseDTO.class))).thenReturn(orderResponseDTO);
            when(modelMapper.map(any(UserDTO.class),eq(OrderResponseDTO.class))).thenReturn(orderResponseDTO);

            OrderResponseDTO actualOrderResponseDTO=orderServiceImpl.getOrder(1L);
            assertNotNull(actualOrderResponseDTO);
        } catch(Exception ex){
            assertTrue(false);
        }
    }

    @Test
    public void testGetOrderNegativeWhenUserIsNotFound(){
        try{
            Order order=new Order();
            order.setId(1L);
            order.setUserId(1L);
            order.setStatus("CREATED");
            order.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));

            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            FeignException notFound = mock(FeignException.NotFound.class);
            when(notFound.getMessage()).thenReturn("User not found with Id: 1");
            when(notFound.status()).thenReturn(404);

            when(userFeignClient.getUser(any())).thenThrow(notFound);

            OrderResponseDTO actualOrderResponseDTO=orderServiceImpl.getOrder(1L);
        } catch(Exception ex){
            assertThat(ex)
                    .isInstanceOf(FeignException.NotFound.class)
                    .hasMessageContaining("User not found with Id: 1");
        }
    }

    @Test
    public void testGetOrderNegativeWhenOrderIsNotFound(){
        try{
            when(orderRepository.findById(any())).thenReturn(Optional.empty());
            OrderResponseDTO actualOrderResponseDTO=orderServiceImpl.getOrder(1L);
        } catch(Exception ex){
            assertThat(ex)
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Order not found with Id: 1");
        }
    }

    @Test
    public void testDeleteOrderPositive(){
        try{
            Order order=new Order();
            order.setId(1L);
            order.setUserId(1L);
            order.setStatus("CREATED");
            order.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));

            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            String result=orderServiceImpl.deleteOrder(1L);
            assertEquals("Order deleted with Id: 1",result);
        } catch(Exception ex){
            assertTrue(false);
        }
    }

    @Test
    public void testDeleteOrderNegativeWhenOrderIsNotFound(){
        try{
            when(orderRepository.findById(any())).thenReturn(Optional.empty());
            String result=orderServiceImpl.deleteOrder(1L);
        } catch(Exception ex){
            assertThat(ex)
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Order not found with Id: 1");
        }
    }

    @Test
    public void testCreateOrderPositive(){
        try{
            OrderDTO orderDTO=new OrderDTO();
            orderDTO.setId(1L);
            orderDTO.setUserId(1L);
            orderDTO.setStatus("CREATED");
            orderDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));

            UserDTO userDTO=new UserDTO();
            userDTO.setId(1L);
            userDTO.setUserName("Aman");
            userDTO.setEmail("Aman@example.com");

            Order order=new Order();
            order.setId(1L);
            order.setUserId(1L);
            order.setStatus("CREATED");
            order.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));

            Order savedOrder=new Order();
            savedOrder.setId(1L);
            savedOrder.setUserId(1L);
            savedOrder.setStatus("CREATED");
            savedOrder.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));

            OrderResponseDTO orderResponseDTO=new OrderResponseDTO();
            orderResponseDTO.setId(1L);
            orderResponseDTO.setUserId(1L);
            orderResponseDTO.setStatus("CREATED");
            orderResponseDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));
            orderResponseDTO.setUserName("Aman");
            orderResponseDTO.setEmail("Aman@example.com");

            when(userFeignClient.getUser(any())).thenReturn(userDTO);
            when(modelMapper.map(any(OrderDTO.class),eq(Order.class))).thenReturn(order);
            when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
            when(modelMapper.map(any(Order.class),eq(OrderResponseDTO.class))).thenReturn(orderResponseDTO);
            when(modelMapper.map(any(UserDTO.class),eq(OrderResponseDTO.class))).thenReturn(orderResponseDTO);

            OrderResponseDTO actualOrderDTO=orderServiceImpl.createOrder(orderDTO);
            assertNotNull(actualOrderDTO);
        }catch(Exception ex){
            assertTrue(false);
        }
    }

    @Test
    public void testCreateOrderNegativeWhenUserIsNotFound(){
        try{
            OrderDTO orderDTO=new OrderDTO();
            orderDTO.setId(1L);
            orderDTO.setUserId(1L);
            orderDTO.setStatus("CREATED");
            orderDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));

            FeignException notFound = mock(FeignException.NotFound.class);
            when(notFound.getMessage()).thenReturn("User not found with Id: 1");
            when(notFound.status()).thenReturn(404);

            when(userFeignClient.getUser(any())).thenThrow(notFound);

            OrderResponseDTO actualOrderDTO=orderServiceImpl.createOrder(orderDTO);
        }catch(Exception ex){
            assertThat(ex)
                    .isInstanceOf(FeignException.NotFound.class)
                    .hasMessageContaining("User not found with Id: 1");
        }
    }

    @Test
    public void testCreateOrderNegativeWhenUserIdIsNegative(){
        OrderDTO orderDTO=new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setUserId(-1L);
        orderDTO.setStatus("CREATED");
        orderDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));

        Set<ConstraintViolation<OrderDTO>> violations = validator.validate(orderDTO);

        assertThat(violations)
                .extracting(v -> v.getMessage())
                .anyMatch(msg -> msg.contains("User_Id must be a positive number"));
    }

    @Test
    public void testCreateOrderNegativeWhenStatusIsInvalid(){
        OrderDTO orderDTO=new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setUserId(1L);
        orderDTO.setStatus("CREATEDPAIDSHIPPEDCANCELLED");
        orderDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));

        Set<ConstraintViolation<OrderDTO>> violations = validator.validate(orderDTO);

        assertThat(violations)
                .extracting(v -> v.getMessage())
                .anyMatch(msg -> msg.contains("Status must be one of: CREATED, PAID, SHIPPED, CANCELLED"));
    }

    @Test
    public void testUpdateOrderStatusPositive(){
        Long orderId=1L;
        String status="PAID";

        try{
            Order order=new Order();
            order.setId(1L);
            order.setUserId(1L);
            order.setStatus("CREATED");
            order.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));

            Order savedorder=new Order();
            savedorder.setId(1L);
            savedorder.setUserId(1L);
            savedorder.setStatus("PAID");
            savedorder.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));

            UserDTO userDTO=new UserDTO();
            userDTO.setId(1L);
            userDTO.setUserName("Aman");
            userDTO.setEmail("Aman@example.com");

            OrderResponseDTO orderResponseDTO=new OrderResponseDTO();
            orderResponseDTO.setId(1L);
            orderResponseDTO.setUserId(1L);
            orderResponseDTO.setStatus("PAID");
            orderResponseDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));
            orderResponseDTO.setUserName("Aman");
            orderResponseDTO.setEmail("Aman@example.com");

            when(orderRepository.findById(any())).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenReturn(savedorder);
            when(userFeignClient.getUser(any())).thenReturn(userDTO);
            when(modelMapper.map(any(Order.class),eq(OrderResponseDTO.class))).thenReturn(orderResponseDTO);
            when(modelMapper.map(any(UserDTO.class),eq(OrderResponseDTO.class))).thenReturn(orderResponseDTO);

            OrderResponseDTO actualorderResponseDTO= orderServiceImpl.updateOrderStatus(orderId,status);
            assertNotNull(actualorderResponseDTO);
        }catch(Exception ex){
            assertTrue(false);
        }
    }

    @Test
    public void testUpdateProductNegativeWhenUserIsNotFound(){
        Long orderId=1L;
        String status="PAID";
        try{
            Order order=new Order();
            order.setId(1L);
            order.setUserId(1L);
            order.setStatus("CREATED");
            order.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));

            Order savedorder=new Order();
            savedorder.setId(1L);
            savedorder.setUserId(1L);
            savedorder.setStatus("PAID");
            savedorder.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));

            when(orderRepository.findById(any())).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenReturn(savedorder);

            FeignException notFound = mock(FeignException.NotFound.class);
            when(notFound.getMessage()).thenReturn("User not found with Id: 1");
            when(notFound.status()).thenReturn(404);

            when(userFeignClient.getUser(any())).thenThrow(notFound);

            OrderResponseDTO actualOrderResponseDTO=orderServiceImpl.updateOrderStatus(orderId,status);
        } catch(Exception ex){
            assertThat(ex)
                    .isInstanceOf(FeignException.NotFound.class)
                    .hasMessageContaining("User not found with Id: 1");
        }
    }

    @Test
    public void testUpdateProductNegativeWhenOrderIsNotFound(){
        Long orderId=1L;
        String status="PAID";
        try{
            when(orderRepository.findById(any())).thenReturn(Optional.empty());
            OrderResponseDTO actualOrderResponseDTO=orderServiceImpl.updateOrderStatus(orderId,status);
        } catch(Exception ex){
            assertThat(ex)
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Order not found with Id: 1");
        }
    }

    @Test
    public void testUpdateProductNegativeWhenStatusIsNotValid(){
        Long orderId=1L;
        String status="CREATEDPAIDSHIPPEDCANCELLED";
        try{
            Order order=new Order();
            order.setId(1L);
            order.setUserId(1L);
            order.setStatus("CREATED");
            order.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));

            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            when(orderRepository.save(any(Order.class))).thenThrow(new RuntimeException("Status must be one of: CREATED, PAID, SHIPPED, CANCELLED"));
            OrderResponseDTO actualOrderResponseDTO=orderServiceImpl.updateOrderStatus(orderId,status);
        } catch(Exception ex){
            assertThat(ex)
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Status must be one of: CREATED, PAID, SHIPPED, CANCELLED");
        }
    }
}
