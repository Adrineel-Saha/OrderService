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

class TestOrderServiceImpl {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserFeignClient userFeignClient;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private OrderServiceImpl orderServiceImpl;

    private Validator validator;

    private static final String USER_NAME = "Aman";
    private static final String USER_EMAIL = "Aman@example.com";
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 2, 1, 10, 0, 0);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @AfterEach
    void tearDown() {}

    // ── Builders ─────────────────────────────────────────────────────────────────

    private Order buildOrder(Long id, String status) {
        Order order = new Order();
        order.setId(id);
        order.setUserId(1L);
        order.setStatus(status);
        order.setCreatedAt(CREATED_AT);
        return order;
    }

    private UserDTO buildUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUserName(USER_NAME);
        userDTO.setEmail(USER_EMAIL);
        return userDTO;
    }

    private OrderResponseDTO buildOrderResponseDTO(String status) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(1L);
        dto.setUserId(1L);
        dto.setStatus(status);
        dto.setCreatedAt(CREATED_AT);
        dto.setUserName(USER_NAME);
        dto.setEmail(USER_EMAIL);
        return dto;
    }

    private OrderDTO buildOrderDTO(String status) {
        OrderDTO dto = new OrderDTO();
        dto.setUserId(1L);
        dto.setStatus(status);
        dto.setCreatedAt(CREATED_AT);
        return dto;
    }

    private void stubGetOrder(Order order, UserDTO userDTO, OrderResponseDTO responseDTO) {
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
        when(userFeignClient.getUser(any())).thenReturn(userDTO);
        when(modelMapper.map(any(Order.class), eq(OrderResponseDTO.class))).thenReturn(responseDTO);
        when(modelMapper.map(any(UserDTO.class), eq(OrderResponseDTO.class))).thenReturn(responseDTO);
    }

    private void stubCreateOrder(UserDTO userDTO, Order mappedOrder, Order savedOrder, OrderResponseDTO responseDTO) {
        when(userFeignClient.getUser(any())).thenReturn(userDTO);
        when(modelMapper.map(any(OrderDTO.class), eq(Order.class))).thenReturn(mappedOrder);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(modelMapper.map(any(Order.class), eq(OrderResponseDTO.class))).thenReturn(responseDTO);
        when(modelMapper.map(any(UserDTO.class), eq(OrderResponseDTO.class))).thenReturn(responseDTO);
    }

    private void stubUpdateOrderStatus(Order order, Order savedOrder, UserDTO userDTO, OrderResponseDTO responseDTO) {
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(userFeignClient.getUser(any())).thenReturn(userDTO);
        when(modelMapper.map(any(Order.class), eq(OrderResponseDTO.class))).thenReturn(responseDTO);
        when(modelMapper.map(any(UserDTO.class), eq(OrderResponseDTO.class))).thenReturn(responseDTO);
    }

    // ── listOrders ────────────────────────────────────────────────────────────────

    @Test
    void testListOrdersPositiveOneOrderFound() {
        List<Order> orderListMock = mock(List.class);
        when(orderRepository.findAll()).thenReturn(orderListMock);
        Order orderMock = mock(Order.class);
        when(orderListMock.stream()).thenReturn(Stream.of(orderMock));
        when(orderMock.getUserId()).thenReturn(1L);
        UserDTO userDTOMock = mock(UserDTO.class);
        when(userFeignClient.getUser(any())).thenReturn(userDTOMock);
        OrderResponseDTO responseMock = mock(OrderResponseDTO.class);
        when(modelMapper.map(any(Order.class), eq(OrderResponseDTO.class))).thenReturn(responseMock);
        when(modelMapper.map(any(UserDTO.class), eq(OrderResponseDTO.class))).thenReturn(responseMock);

        assertEquals(1, orderServiceImpl.listOrders().size());
    }

    @Test
    void testListOrdersPositiveMultipleOrdersFound() {
        List<Order> orderListMock = mock(List.class);
        when(orderRepository.findAll()).thenReturn(orderListMock);
        Order m1 = mock(Order.class);
        Order m2 = mock(Order.class);
        when(orderListMock.stream()).thenReturn(Stream.of(m1, m2));
        when(m1.getUserId()).thenReturn(1L);
        when(m2.getUserId()).thenReturn(2L);
        UserDTO userDTOMock = mock(UserDTO.class);
        when(userFeignClient.getUser(any())).thenReturn(userDTOMock);
        OrderResponseDTO responseMock = mock(OrderResponseDTO.class);
        when(modelMapper.map(any(Order.class), eq(OrderResponseDTO.class))).thenReturn(responseMock);
        when(modelMapper.map(any(UserDTO.class), eq(OrderResponseDTO.class))).thenReturn(responseMock);

        assertTrue(orderServiceImpl.listOrders().size() > 1);
    }

    @Test
    void testListOrdersPositiveAssertListSize() {
        List<Order> orderListMock = mock(List.class);
        when(orderRepository.findAll()).thenReturn(orderListMock);
        Order m1 = mock(Order.class);
        Order m2 = mock(Order.class);
        Order m3 = mock(Order.class);
        when(orderListMock.stream()).thenReturn(Stream.of(m1, m2, m3));
        when(m1.getUserId()).thenReturn(1L);
        when(m2.getUserId()).thenReturn(2L);
        when(m3.getUserId()).thenReturn(3L);
        UserDTO userDTOMock = mock(UserDTO.class);
        when(userFeignClient.getUser(any())).thenReturn(userDTOMock);
        OrderResponseDTO responseMock = mock(OrderResponseDTO.class);
        when(modelMapper.map(any(Order.class), eq(OrderResponseDTO.class))).thenReturn(responseMock);
        when(modelMapper.map(any(UserDTO.class), eq(OrderResponseDTO.class))).thenReturn(responseMock);

        assertEquals(3, orderServiceImpl.listOrders().size());
    }

    @Test
    void testListOrdersNegativeWhenUserIsNotFound() {
        Order orderMock = mock(Order.class);
        when(orderRepository.findAll()).thenReturn(List.of(orderMock));
        when(orderMock.getUserId()).thenReturn(1L);
        FeignException notFound = mock(FeignException.NotFound.class);
        when(notFound.getMessage()).thenReturn("User not found with Id: 1");
        when(notFound.status()).thenReturn(404);
        when(userFeignClient.getUser(any())).thenThrow(notFound);

        assertThatThrownBy(() -> orderServiceImpl.listOrders())
                .isInstanceOf(FeignException.NotFound.class)
                .hasMessage("User not found with Id: 1");
    }

    @Test
    void testListOrdersNegativeWhenListIsEmpty() {
        when(orderRepository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> orderServiceImpl.listOrders())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order List is Empty");
    }

    // ── listOrdersByUser ──────────────────────────────────────────────────────────

    @Test
    void testListOrdersByUsersPositiveOneOrderFound() {
        UserDTO userDTOMock = mock(UserDTO.class);
        when(userFeignClient.getUser(any())).thenReturn(userDTOMock);
        List<Order> orderListMock = mock(List.class);
        when(orderRepository.findByUserId(any())).thenReturn(orderListMock);
        Order orderMock = mock(Order.class);
        when(orderListMock.stream()).thenReturn(Stream.of(orderMock));
        OrderResponseDTO responseMock = mock(OrderResponseDTO.class);
        when(modelMapper.map(any(Order.class), eq(OrderResponseDTO.class))).thenReturn(responseMock);
        when(modelMapper.map(any(UserDTO.class), eq(OrderResponseDTO.class))).thenReturn(responseMock);

        assertEquals(1, orderServiceImpl.listOrdersByUser(1L).size());
    }

    @Test
    void testListOrdersByUserPositiveMultipleOrdersFound() {
        UserDTO userDTOMock = mock(UserDTO.class);
        when(userFeignClient.getUser(any())).thenReturn(userDTOMock);
        List<Order> orderListMock = mock(List.class);
        when(orderRepository.findByUserId(any())).thenReturn(orderListMock);
        Order m1 = mock(Order.class);
        Order m2 = mock(Order.class);
        when(orderListMock.stream()).thenReturn(Stream.of(m1, m2));
        OrderResponseDTO responseMock = mock(OrderResponseDTO.class);
        when(modelMapper.map(any(Order.class), eq(OrderResponseDTO.class))).thenReturn(responseMock);
        when(modelMapper.map(any(UserDTO.class), eq(OrderResponseDTO.class))).thenReturn(responseMock);

        assertTrue(orderServiceImpl.listOrdersByUser(1L).size() > 1);
    }

    @Test
    void testListOrdersByUserPositiveAssertListSize() {
        UserDTO userDTOMock = mock(UserDTO.class);
        when(userFeignClient.getUser(any())).thenReturn(userDTOMock);
        List<Order> orderListMock = mock(List.class);
        when(orderRepository.findByUserId(any())).thenReturn(orderListMock);
        Order m1 = mock(Order.class);
        Order m2 = mock(Order.class);
        when(orderListMock.stream()).thenReturn(Stream.of(m1, m2));
        OrderResponseDTO responseMock = mock(OrderResponseDTO.class);
        when(modelMapper.map(any(Order.class), eq(OrderResponseDTO.class))).thenReturn(responseMock);
        when(modelMapper.map(any(UserDTO.class), eq(OrderResponseDTO.class))).thenReturn(responseMock);

        assertEquals(2, orderServiceImpl.listOrdersByUser(1L).size());
    }

    @Test
    void testListOrdersByUserNegativeWhenUserIsNotFound() {
        FeignException notFound = mock(FeignException.NotFound.class);
        when(notFound.getMessage()).thenReturn("User not found with Id: 1");
        when(notFound.status()).thenReturn(404);
        when(userFeignClient.getUser(any())).thenThrow(notFound);

        assertThatThrownBy(() -> orderServiceImpl.listOrdersByUser(1L))
                .isInstanceOf(FeignException.NotFound.class)
                .hasMessage("User not found with Id: 1");
    }

    @Test
    void testListOrdersByUserNegativeWhenListIsEmpty() {
        when(orderRepository.findByUserId(any())).thenReturn(List.of());

        assertThatThrownBy(() -> orderServiceImpl.listOrdersByUser(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order List is Empty");
    }

    // ── getOrder ──────────────────────────────────────────────────────────────────

    @Test
    void testGetOrderPositive() {
        OrderResponseDTO responseDTO = buildOrderResponseDTO("CREATED");
        stubGetOrder(buildOrder(1L, "CREATED"), buildUserDTO(), responseDTO);

        assertNotNull(orderServiceImpl.getOrder(1L));
    }

    @Test
    void testGetOrderPositiveAssertStatus() {
        OrderResponseDTO responseDTO = buildOrderResponseDTO("CREATED");
        stubGetOrder(buildOrder(1L, "CREATED"), buildUserDTO(), responseDTO);

        assertEquals("CREATED", orderServiceImpl.getOrder(1L).getStatus());
    }

    @Test
    void testGetOrderPositiveAssertUserId() {
        OrderResponseDTO responseDTO = buildOrderResponseDTO("CREATED");
        stubGetOrder(buildOrder(1L, "CREATED"), buildUserDTO(), responseDTO);

        assertEquals(1L, orderServiceImpl.getOrder(1L).getUserId());
    }

    @Test
    void testGetOrderPositiveAssertEmail() {
        OrderResponseDTO responseDTO = buildOrderResponseDTO("CREATED");
        stubGetOrder(buildOrder(1L, "CREATED"), buildUserDTO(), responseDTO);

        assertEquals(USER_EMAIL, orderServiceImpl.getOrder(1L).getEmail());
    }

    @Test
    void testGetOrderPositiveAssertUserName() {
        OrderResponseDTO responseDTO = buildOrderResponseDTO("CREATED");
        stubGetOrder(buildOrder(1L, "CREATED"), buildUserDTO(), responseDTO);

        assertEquals(USER_NAME, orderServiceImpl.getOrder(1L).getUserName());
    }

    @Test
    void testGetOrderNegativeWhenUserIsNotFound() {
        when(orderRepository.findById(any())).thenReturn(Optional.of(buildOrder(1L, "CREATED")));
        FeignException notFound = mock(FeignException.NotFound.class);
        when(notFound.getMessage()).thenReturn("User not found with Id: 1");
        when(notFound.status()).thenReturn(404);
        when(userFeignClient.getUser(any())).thenThrow(notFound);

        assertThatThrownBy(() -> orderServiceImpl.getOrder(1L))
                .isInstanceOf(FeignException.NotFound.class)
                .hasMessageContaining("User not found with Id: 1");
    }

    @Test
    void testGetOrderNegativeWhenOrderIsNotFound() {
        when(orderRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderServiceImpl.getOrder(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found with Id: 1");
    }

    // ── createOrder ───────────────────────────────────────────────────────────────

    @Test
    void testCreateOrderPositive() {
        OrderDTO orderDTO = buildOrderDTO("CREATED");
        OrderResponseDTO responseDTO = buildOrderResponseDTO("CREATED");
        stubCreateOrder(buildUserDTO(), new Order(), buildOrder(1L, "CREATED"), responseDTO);

        assertNotNull(orderServiceImpl.createOrder(orderDTO));
    }

    @Test
    void testCreateOrderPositiveAssertStatus() {
        OrderDTO orderDTO = buildOrderDTO("CREATED");
        OrderResponseDTO responseDTO = buildOrderResponseDTO("CREATED");
        stubCreateOrder(buildUserDTO(), new Order(), buildOrder(1L, "CREATED"), responseDTO);

        assertEquals("CREATED", orderServiceImpl.createOrder(orderDTO).getStatus());
    }

    @Test
    void testCreateOrderPositiveAssertEmail() {
        OrderDTO orderDTO = buildOrderDTO("CREATED");
        OrderResponseDTO responseDTO = buildOrderResponseDTO("CREATED");
        stubCreateOrder(buildUserDTO(), new Order(), buildOrder(1L, "CREATED"), responseDTO);

        assertEquals(USER_EMAIL, orderServiceImpl.createOrder(orderDTO).getEmail());
    }

    @Test
    void testCreateOrderPositiveAssertUserName() {
        OrderDTO orderDTO = buildOrderDTO("CREATED");
        OrderResponseDTO responseDTO = buildOrderResponseDTO("CREATED");
        stubCreateOrder(buildUserDTO(), new Order(), buildOrder(1L, "CREATED"), responseDTO);

        assertEquals(USER_NAME, orderServiceImpl.createOrder(orderDTO).getUserName());
    }

    @Test
    void testCreateOrderNegativeWhenUserIsNotFound() {
        FeignException notFound = mock(FeignException.NotFound.class);
        when(notFound.getMessage()).thenReturn("User not found with Id: 1");
        when(notFound.status()).thenReturn(404);
        when(userFeignClient.getUser(any())).thenThrow(notFound);

        assertThatThrownBy(() -> orderServiceImpl.createOrder(buildOrderDTO("CREATED")))
                .isInstanceOf(FeignException.NotFound.class)
                .hasMessageContaining("User not found with Id: 1");
    }

    @Test
    void testCreateOrderNegativeWhenUserIdIsNegative() {
        OrderDTO orderDTO = buildOrderDTO("CREATED");
        orderDTO.setUserId(-1L);

        Set<ConstraintViolation<OrderDTO>> violations = validator.validate(orderDTO);
        assertThat(violations).extracting(v -> v.getMessage())
                .anyMatch(msg -> msg.contains("User_Id must be a positive number"));
    }

    @Test
    void testCreateOrderNegativeWhenUserIdIsZero() {
        OrderDTO orderDTO = buildOrderDTO("CREATED");
        orderDTO.setUserId(0L);

        Set<ConstraintViolation<OrderDTO>> violations = validator.validate(orderDTO);
        assertThat(violations).extracting(v -> v.getMessage())
                .anyMatch(msg -> msg.contains("User_Id must be a positive number"));
    }

    @Test
    void testCreateOrderNegativeWhenUserIdIsNull() {
        OrderDTO orderDTO = buildOrderDTO("CREATED");
        orderDTO.setUserId(null);

        Set<ConstraintViolation<OrderDTO>> violations = validator.validate(orderDTO);
        assertThat(violations).extracting(v -> v.getMessage())
                .anyMatch(msg -> msg.contains("User_Id is required"));
    }

    @Test
    void testCreateOrderNegativeWhenStatusIsInvalid() {
        OrderDTO orderDTO = buildOrderDTO("INVALID_STATUS");

        Set<ConstraintViolation<OrderDTO>> violations = validator.validate(orderDTO);
        assertThat(violations).extracting(v -> v.getMessage())
                .anyMatch(msg -> msg.contains("Status must be one of: CREATED, PAID, SHIPPED, CANCELLED"));
    }

    // ── updateOrderStatus ─────────────────────────────────────────────────────────

    @Test
    void testUpdateOrderStatusPositive() {
        OrderResponseDTO responseDTO = buildOrderResponseDTO("PAID");
        stubUpdateOrderStatus(buildOrder(1L, "CREATED"), buildOrder(1L, "PAID"), buildUserDTO(), responseDTO);

        assertNotNull(orderServiceImpl.updateOrderStatus(1L, "PAID"));
    }

    @Test
    void testUpdateOrderStatusPositiveAssertUpdatedStatus() {
        OrderResponseDTO responseDTO = buildOrderResponseDTO("SHIPPED");
        responseDTO.setStatus("SHIPPED");
        stubUpdateOrderStatus(buildOrder(1L, "CREATED"), buildOrder(1L, "SHIPPED"), buildUserDTO(), responseDTO);

        assertEquals("SHIPPED", orderServiceImpl.updateOrderStatus(1L, "SHIPPED").getStatus());
    }

    @Test
    void testUpdateOrderStatusPositiveAssertEmail() {
        OrderResponseDTO responseDTO = buildOrderResponseDTO("PAID");
        stubUpdateOrderStatus(buildOrder(1L, "CREATED"), buildOrder(1L, "PAID"), buildUserDTO(), responseDTO);

        assertEquals(USER_EMAIL, orderServiceImpl.updateOrderStatus(1L, "PAID").getEmail());
    }

    @Test
    void testUpdateProductNegativeWhenUserIsNotFound() {
        when(orderRepository.findById(any())).thenReturn(Optional.of(buildOrder(1L, "CREATED")));
        when(orderRepository.save(any(Order.class))).thenReturn(buildOrder(1L, "PAID"));
        FeignException notFound = mock(FeignException.NotFound.class);
        when(notFound.getMessage()).thenReturn("User not found with Id: 1");
        when(notFound.status()).thenReturn(404);
        when(userFeignClient.getUser(any())).thenThrow(notFound);

        assertThatThrownBy(() -> orderServiceImpl.updateOrderStatus(1L, "PAID"))
                .isInstanceOf(FeignException.NotFound.class)
                .hasMessageContaining("User not found with Id: 1");
    }

    @Test
    void testUpdateProductNegativeWhenOrderIsNotFound() {
        when(orderRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderServiceImpl.updateOrderStatus(1L, "PAID"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found with Id: 1");
    }

    @Test
    void testUpdateProductNegativeWhenStatusIsNotValid() {
        when(orderRepository.findById(any())).thenReturn(Optional.of(buildOrder(1L, "CREATED")));
        when(orderRepository.save(any(Order.class)))
                .thenThrow(new RuntimeException("Status must be one of: CREATED, PAID, SHIPPED, CANCELLED"));

        assertThatThrownBy(() -> orderServiceImpl.updateOrderStatus(1L, "INVALID"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Status must be one of: CREATED, PAID, SHIPPED, CANCELLED");
    }

    // ── deleteOrder ───────────────────────────────────────────────────────────────

    @Test
    void testDeleteOrderPositiveAssertMessage() {
        when(orderRepository.findById(any())).thenReturn(Optional.of(buildOrder(1L, "CREATED")));

        assertEquals("Order deleted with Id: 1", orderServiceImpl.deleteOrder(1L));
    }

    @Test
    void testDeleteOrderNegativeWhenOrderIsNotFound() {
        when(orderRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderServiceImpl.deleteOrder(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found with Id: 1");
    }

    // ── Circuit breaker fallbacks ──────────────────────────────────────────────────

    @Test
    void testCreateOrderCircuitBreakerFallback() {
        OrderDTO orderDTO = buildOrderDTO("CREATED");
        Order order = new Order();
        order.setUserId(1L);
        Order savedOrder = buildOrder(1L, "CREATED");
        OrderResponseDTO responseDTO = buildOrderResponseDTO("CREATED");
        FeignException feignException = mock(FeignException.class);

        when(modelMapper.map(orderDTO, Order.class)).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(modelMapper.map(savedOrder, OrderResponseDTO.class)).thenReturn(responseDTO);

        OrderResponseDTO result = orderServiceImpl.createOrderGetDefaultUser(orderDTO, feignException);
        assertNotNull(result);
        assertEquals(USER_NAME, result.getUserName());
    }

    @Test
    void testGetOrderCircuitBreakerFallback() {
        Order order = buildOrder(1L, "CREATED");
        OrderResponseDTO responseDTO = buildOrderResponseDTO("CREATED");
        FeignException feignException = mock(FeignException.class);

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
        when(modelMapper.map(order, OrderResponseDTO.class)).thenReturn(responseDTO);

        OrderResponseDTO result = orderServiceImpl.getOrderGetDefaultUser(1L, feignException);
        assertNotNull(result);
        assertEquals(USER_NAME, result.getUserName());
    }

    @Test
    void testListOrdersCircuitBreakerFallback() {
        Order order = buildOrder(1L, "CREATED");
        OrderResponseDTO responseDTO = buildOrderResponseDTO("CREATED");
        FeignException feignException = mock(FeignException.class);

        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(modelMapper.map(order, OrderResponseDTO.class)).thenReturn(responseDTO);

        List<OrderResponseDTO> result = orderServiceImpl.listOrdersGetDefaultUser(feignException);
        assertFalse(result.isEmpty());
        assertEquals(USER_NAME, result.get(0).getUserName());
    }

    @Test
    void testListOrdersByUserCircuitBreakerFallback() {
        Long userId = 1L;
        Order order = buildOrder(1L, "CREATED");
        OrderResponseDTO responseDTO = buildOrderResponseDTO("CREATED");
        FeignException feignException = mock(FeignException.class);

        when(orderRepository.findByUserId(userId)).thenReturn(List.of(order));
        when(modelMapper.map(order, OrderResponseDTO.class)).thenReturn(responseDTO);

        List<OrderResponseDTO> result = orderServiceImpl.listOrdersByUserGetDefaultUser(userId, feignException);
        assertFalse(result.isEmpty());
        assertEquals(USER_NAME, result.get(0).getUserName());
    }

    @Test
    void testUpdateOrderStatusCircuitBreakerFallback() {
        Long orderId = 1L;
        Order order = buildOrder(orderId, "CREATED");
        Order savedOrder = buildOrder(orderId, "PAID");
        OrderResponseDTO responseDTO = buildOrderResponseDTO("PAID");
        FeignException feignException = mock(FeignException.class);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(modelMapper.map(savedOrder, OrderResponseDTO.class)).thenReturn(responseDTO);

        OrderResponseDTO result = orderServiceImpl.updateOrderStatusGetDefaultUser(orderId, "PAID", feignException);
        assertNotNull(result);
        assertEquals(USER_NAME, result.getUserName());
    }
}
