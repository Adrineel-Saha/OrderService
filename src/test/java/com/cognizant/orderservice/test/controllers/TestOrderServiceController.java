package com.cognizant.orderservice.test.controllers;

import com.cognizant.orderservice.controllers.OrderServiceController;
import com.cognizant.orderservice.dtos.OrderDTO;
import com.cognizant.orderservice.dtos.OrderItemDTO;
import com.cognizant.orderservice.dtos.OrderItemResponseDTO;
import com.cognizant.orderservice.dtos.OrderResponseDTO;
import com.cognizant.orderservice.main.OrderServiceApplication;
import com.cognizant.orderservice.services.OrderItemService;
import com.cognizant.orderservice.services.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes= OrderServiceApplication.class)
@ActiveProfiles("test")
public class TestOrderServiceController {
    @Mock
    private OrderService orderService;
    @Mock
    private OrderItemService orderItemService;
    @InjectMocks
    private OrderServiceController orderServiceController;

    @Autowired
    private LocalValidatorFactoryBean validator;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {

    }

    @Test
    void testListOrdersPositiveAssertReturnValue() {
        List<OrderResponseDTO> orderResponseDTOList=new ArrayList<>();

        OrderResponseDTO orderResponseDTO=new OrderResponseDTO();
        orderResponseDTO.setId(1L);
        orderResponseDTO.setUserId(1L);
        orderResponseDTO.setStatus("CREATED");
        orderResponseDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));
        orderResponseDTO.setUserName("Aman");
        orderResponseDTO.setEmail("Aman@example.com");

        orderResponseDTOList.add(orderResponseDTO);

        try {
            when(orderService.listOrders()).thenReturn(orderResponseDTOList);
            ResponseEntity<List<OrderResponseDTO>> responseEntity=orderServiceController.listOrders();
            List<OrderResponseDTO> actualOrderResponseDTOList=responseEntity.getBody();
            assertTrue(actualOrderResponseDTOList.size()>0);
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testListOrdersPositiveAssertStatusCode() {
        List<OrderResponseDTO> orderResponseDTOList=new ArrayList<>();

        OrderResponseDTO orderResponseDTO=new OrderResponseDTO();
        orderResponseDTO.setId(1L);
        orderResponseDTO.setUserId(1L);
        orderResponseDTO.setStatus("CREATED");
        orderResponseDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));
        orderResponseDTO.setUserName("Aman");
        orderResponseDTO.setEmail("Aman@example.com");

        orderResponseDTOList.add(orderResponseDTO);

        try {
            when(orderService.listOrders()).thenReturn(orderResponseDTOList);
            ResponseEntity<List<OrderResponseDTO>> responseEntity=orderServiceController.listOrders();
            assertEquals(200,responseEntity.getStatusCode().value());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testListOrdersNegativeAssertReturnValue() {
        List<OrderResponseDTO> orderResponseDTOList=new ArrayList<>();
        try {
            when(orderService.listOrders()).thenReturn(orderResponseDTOList);
            ResponseEntity<List<OrderResponseDTO>> responseEntity=orderServiceController.listOrders();
            assertNull(responseEntity.getBody());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testListOrdersNegativeAssertStatusCode() {
        List<OrderResponseDTO> orderResponseDTOList=new ArrayList<>();
        try {
            when(orderService.listOrders()).thenReturn(orderResponseDTOList);
            ResponseEntity<List<OrderResponseDTO>> responseEntity=orderServiceController.listOrders();
            assertEquals(400,responseEntity.getStatusCode().value());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testListOrdersByUserPositiveAssertReturnValue() {
        List<OrderResponseDTO> orderResponseDTOList=new ArrayList<>();

        OrderResponseDTO orderResponseDTO=new OrderResponseDTO();
        orderResponseDTO.setId(1L);
        orderResponseDTO.setUserId(1L);
        orderResponseDTO.setStatus("CREATED");
        orderResponseDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));
        orderResponseDTO.setUserName("Aman");
        orderResponseDTO.setEmail("Aman@example.com");

        orderResponseDTOList.add(orderResponseDTO);

        try {
            when(orderService.listOrdersByUser(any())).thenReturn(orderResponseDTOList);
            ResponseEntity<List<OrderResponseDTO>> responseEntity=orderServiceController.listOrdersByUser(1L);
            List<OrderResponseDTO> actualOrderResponseDTOList=responseEntity.getBody();
            assertTrue(actualOrderResponseDTOList.size()>0);
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testListOrdersByUserPositiveAssertStatusCode() {
        List<OrderResponseDTO> orderResponseDTOList=new ArrayList<>();

        OrderResponseDTO orderResponseDTO=new OrderResponseDTO();
        orderResponseDTO.setId(1L);
        orderResponseDTO.setUserId(1L);
        orderResponseDTO.setStatus("CREATED");
        orderResponseDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));
        orderResponseDTO.setUserName("Aman");
        orderResponseDTO.setEmail("Aman@example.com");

        orderResponseDTOList.add(orderResponseDTO);

        try {
            when(orderService.listOrdersByUser(any())).thenReturn(orderResponseDTOList);
            ResponseEntity<List<OrderResponseDTO>> responseEntity=orderServiceController.listOrdersByUser(1L);
            assertEquals(200,responseEntity.getStatusCode().value());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testListOrdersByUserNegativeAssertReturnValue() {
        List<OrderResponseDTO> orderResponseDTOList=new ArrayList<>();
        try {
            when(orderService.listOrdersByUser(any())).thenReturn(orderResponseDTOList);
            ResponseEntity<List<OrderResponseDTO>> responseEntity=orderServiceController.listOrdersByUser(1L);
            assertNull(responseEntity.getBody());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testListOrdersByUserNegativeAssertStatusCode() {
        List<OrderResponseDTO> orderResponseDTOList=new ArrayList<>();
        try {
            when(orderService.listOrdersByUser(any())).thenReturn(orderResponseDTOList);
            ResponseEntity<List<OrderResponseDTO>> responseEntity=orderServiceController.listOrdersByUser(1L);
            assertEquals(400,responseEntity.getStatusCode().value());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testGetOrderPositiveAssertReturnValue() {
        OrderResponseDTO orderResponseDTO=new OrderResponseDTO();
        orderResponseDTO.setId(1L);
        orderResponseDTO.setUserId(1L);
        orderResponseDTO.setStatus("CREATED");
        orderResponseDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));
        orderResponseDTO.setUserName("Aman");
        orderResponseDTO.setEmail("Aman@example.com");

        try {
            when(orderService.getOrder(any())).thenReturn(orderResponseDTO);
            ResponseEntity<OrderResponseDTO> responseEntity=orderServiceController.getOrder(1L);
            OrderResponseDTO actualOrderResponseDTO=responseEntity.getBody();
            assertNotNull(actualOrderResponseDTO);
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testGetOrderPositiveAssertStatusCode() {
        OrderResponseDTO orderResponseDTO=new OrderResponseDTO();
        orderResponseDTO.setId(1L);
        orderResponseDTO.setUserId(1L);
        orderResponseDTO.setStatus("CREATED");
        orderResponseDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));
        orderResponseDTO.setUserName("Aman");
        orderResponseDTO.setEmail("Aman@example.com");

        try {
            when(orderService.getOrder(any())).thenReturn(orderResponseDTO);
            ResponseEntity<OrderResponseDTO> responseEntity=orderServiceController.getOrder(1L);
            assertEquals(200,responseEntity.getStatusCode().value());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testGetOrderNegativeAssertReturnValue() {
        OrderResponseDTO orderResponseDTO=null;
        try {
            when(orderService.getOrder(any())).thenReturn(orderResponseDTO);
            ResponseEntity<OrderResponseDTO> responseEntity=orderServiceController.getOrder(1L);
            assertNull(responseEntity.getBody());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testGetOrderNegativeAssertStatusCode() {
        OrderResponseDTO orderResponseDTO=null;
        try {
            when(orderService.getOrder(any())).thenReturn(orderResponseDTO);
            ResponseEntity<OrderResponseDTO> responseEntity=orderServiceController.getOrder(1L);
            assertEquals(400,responseEntity.getStatusCode().value());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testDeleteOrderPositiveAssertReturnValue() {
        OrderResponseDTO orderResponseDTO=new OrderResponseDTO();
        orderResponseDTO.setId(1L);
        orderResponseDTO.setUserId(1L);
        orderResponseDTO.setStatus("CREATED");
        orderResponseDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));
        orderResponseDTO.setUserName("Aman");
        orderResponseDTO.setEmail("Aman@example.com");

        try {
            when(orderService.deleteOrder(any())).thenReturn("Order deleted with Id: " + orderResponseDTO.getId());
            ResponseEntity<String> responseEntity=orderServiceController.deleteOrder(1L);
            String result=responseEntity.getBody();
            assertNotNull(result);
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testDeleteOrderPositiveAssertStatusCode() {
        OrderResponseDTO orderResponseDTO=new OrderResponseDTO();
        orderResponseDTO.setId(1L);
        orderResponseDTO.setUserId(1L);
        orderResponseDTO.setStatus("CREATED");
        orderResponseDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));
        orderResponseDTO.setUserName("Aman");
        orderResponseDTO.setEmail("Aman@example.com");

        try {
            when(orderService.deleteOrder(any())).thenReturn("Order deleted with Id: " + orderResponseDTO.getId());
            ResponseEntity<String> responseEntity=orderServiceController.deleteOrder(1L);
            assertEquals(200,responseEntity.getStatusCode().value());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testDeleteOrderNegativeAssertReturnValue() {
        String result=null;
        try {
            when(orderService.deleteOrder(any())).thenReturn(result);
            ResponseEntity<String> responseEntity=orderServiceController.deleteOrder(1L);
            assertNull(responseEntity.getBody());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testDeleteOrderNegativeAssertStatusCode() {
        String result=null;
        try {
            when(orderService.deleteOrder(any())).thenReturn(result);
            ResponseEntity<String> responseEntity=orderServiceController.deleteOrder(1L);
            assertEquals(400,responseEntity.getStatusCode().value());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testCreateOrderWhenOrderIsValid() {
        OrderDTO orderDTO=new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setUserId(1L);
        orderDTO.setStatus("CREATED");
        orderDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));

        validator.validate(orderDTO).stream().forEach((constraintViolation)->assertNull(constraintViolation));
    }

    @Test
    void testCreateOrderPositiveAssertReturnValue() {
        OrderDTO orderDTO=new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setUserId(1L);
        orderDTO.setStatus("CREATED");
        orderDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));

        OrderResponseDTO orderResponseDTO=new OrderResponseDTO();
        orderResponseDTO.setId(1L);
        orderResponseDTO.setUserId(1L);
        orderResponseDTO.setStatus("CREATED");
        orderResponseDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));
        orderResponseDTO.setUserName("Aman");
        orderResponseDTO.setEmail("Aman@example.com");

        try {
            when(orderService.createOrder(any(OrderDTO.class))).thenReturn(orderResponseDTO);
            ResponseEntity<OrderResponseDTO> responseEntity=orderServiceController.createOrder(orderDTO);
            OrderResponseDTO actualOrderResponseDTO=responseEntity.getBody();
            assertNotNull(actualOrderResponseDTO);
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testCreateOrderPositiveAssertStatusCode() {
        OrderDTO orderDTO=new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setUserId(1L);
        orderDTO.setStatus("CREATED");
        orderDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));

        OrderResponseDTO orderResponseDTO=new OrderResponseDTO();
        orderResponseDTO.setId(1L);
        orderResponseDTO.setUserId(1L);
        orderResponseDTO.setStatus("CREATED");
        orderResponseDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));
        orderResponseDTO.setUserName("Aman");
        orderResponseDTO.setEmail("Aman@example.com");

        try {
            when(orderService.createOrder(any(OrderDTO.class))).thenReturn(orderResponseDTO);
            ResponseEntity<OrderResponseDTO> responseEntity=orderServiceController.createOrder(orderDTO);
            assertEquals(201,responseEntity.getStatusCode().value());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testCreateOrderWhenOrderIsNotValid() {
        OrderDTO orderDTO=new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setUserId(-1L);
        orderDTO.setStatus("CREATEDPAIDSHIPPEDCANCELLED");
        orderDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));

        validator.validate(orderDTO).stream().forEach((constraintViolation)->assertNotNull(constraintViolation));
    }

    @Test
    void testCreateOrderNegativeAssertReturnValue() {
        OrderDTO orderDTO=null;
        OrderResponseDTO orderResponseDTO=null;

        try {
            when(orderService.createOrder(any(OrderDTO.class))).thenReturn(orderResponseDTO);
            ResponseEntity<OrderResponseDTO> responseEntity=orderServiceController.createOrder(orderDTO);
            OrderResponseDTO actualOrderResponseDTO=responseEntity.getBody();
            assertNull(actualOrderResponseDTO);
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testCreateOrderNegativeAssertStatusCode() {
        OrderDTO orderDTO=null;
        OrderResponseDTO orderResponseDTO=null;

        try {
            when(orderService.createOrder(any(OrderDTO.class))).thenReturn(orderResponseDTO);
            ResponseEntity<OrderResponseDTO> responseEntity=orderServiceController.createOrder(orderDTO);
            assertEquals(400,responseEntity.getStatusCode().value());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testUpdateUpdateOrderStatusWhenOrderStatusIsValid() {
        // Arrange
        String status = "SHIPPED";

        // Act: validate only the "status" property on OrderDTO class (no instance created)
        var violations = validator.validateValue(OrderDTO.class, "status", status);

        // Assert
        assertTrue(violations.isEmpty(), "Expected no violations for a valid status");
    }

    @Test
    void testUpdateUpdateOrderStatusPositiveAssertReturnValue() {
        OrderResponseDTO orderResponseDTO=new OrderResponseDTO();
        orderResponseDTO.setId(1L);
        orderResponseDTO.setUserId(1L);
        orderResponseDTO.setStatus("SHIPPED");
        orderResponseDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));
        orderResponseDTO.setUserName("Aman");
        orderResponseDTO.setEmail("Aman@example.com");

        try {
            when(orderService.updateOrderStatus(any(),anyString())).thenReturn(orderResponseDTO);
            ResponseEntity<OrderResponseDTO> responseEntity=orderServiceController.updateOrderStatus(1L,"SHIPPED");
            OrderResponseDTO actualOrderResponseDTO=responseEntity.getBody();
            assertNotNull(actualOrderResponseDTO);
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testUpdateUpdateOrderStatusPositiveAssertStatusCode() {
        OrderResponseDTO orderResponseDTO=new OrderResponseDTO();
        orderResponseDTO.setId(1L);
        orderResponseDTO.setUserId(1L);
        orderResponseDTO.setStatus("SHIPPED");
        orderResponseDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));
        orderResponseDTO.setUserName("Aman");
        orderResponseDTO.setEmail("Aman@example.com");

        try {
            when(orderService.updateOrderStatus(any(),anyString())).thenReturn(orderResponseDTO);
            ResponseEntity<OrderResponseDTO> responseEntity=orderServiceController.updateOrderStatus(1L,"SHIPPED");
            assertEquals(202,responseEntity.getStatusCode().value());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testUpdateUpdateOrderStatusWhenOrderStatusIsNotValid() {
        // Arrange
        String status = "CREATEDPAIDSHIPPEDCANCELLED"; // invalid, does not match the alternation

        // Act
        var violations = validator.validateValue(OrderDTO.class, "status", status);

        // Assert
        assertFalse(violations.isEmpty(), "Expected violations for an invalid status");
        // (Optional) verify which property failed and the message from your DTO
        violations.forEach(v -> {
            assertEquals("status", v.getPropertyPath().toString());
            assertEquals("Status must be one of: CREATED, PAID, SHIPPED, CANCELLED", v.getMessage());
        });
    }

    @Test
    void testUpdateUpdateOrderStatusNegativeAssertReturnValue() {
        OrderResponseDTO orderResponseDTO=null;

        try {
            when(orderService.updateOrderStatus(any(),anyString())).thenReturn(orderResponseDTO);
            ResponseEntity<OrderResponseDTO> responseEntity=orderServiceController.updateOrderStatus(1L,"CREATEDPAIDSHIPPEDCANCELLED");
            OrderResponseDTO actualOrderResponseDTO=responseEntity.getBody();
            assertNull(actualOrderResponseDTO);
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testUpdateUpdateOrderStatusNegativeAssertStatusCode() {
        OrderResponseDTO orderResponseDTO=null;

        try {
            when(orderService.updateOrderStatus(any(),anyString())).thenReturn(orderResponseDTO);
            ResponseEntity<OrderResponseDTO> responseEntity=orderServiceController.updateOrderStatus(1L,"CREATEDPAIDSHIPPEDCANCELLED");
            assertEquals(400,responseEntity.getStatusCode().value());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testListItemsPositiveAssertReturnValue() {
        List<OrderItemResponseDTO> orderItemResponseDTOList=new ArrayList<>();

        OrderItemResponseDTO orderItemResponseDTO=new OrderItemResponseDTO();
        orderItemResponseDTO.setId(1L);
        orderItemResponseDTO.setProductId(1L);
        orderItemResponseDTO.setQuantity(2);
        orderItemResponseDTO.setPrice(499.5);
        orderItemResponseDTO.setOrderId(1L);
        orderItemResponseDTO.setName("Mechanical Keyboard");
        orderItemResponseDTO.setDescription("RGB backlit mechanical keyboard with blue switches.");
        orderItemResponseDTO.setStock(200);

        orderItemResponseDTOList.add(orderItemResponseDTO);

        try {
            when(orderItemService.listItems()).thenReturn(orderItemResponseDTOList);
            ResponseEntity<List<OrderItemResponseDTO>> responseEntity=orderServiceController.listItems();
            List<OrderItemResponseDTO> actualOrderItemResponseDTOList=responseEntity.getBody();
            assertTrue(actualOrderItemResponseDTOList.size()>0);
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testListItemsPositiveAssertStatusCode() {
        List<OrderItemResponseDTO> orderItemResponseDTOList=new ArrayList<>();

        OrderItemResponseDTO orderItemResponseDTO=new OrderItemResponseDTO();
        orderItemResponseDTO.setId(1L);
        orderItemResponseDTO.setProductId(1L);
        orderItemResponseDTO.setQuantity(2);
        orderItemResponseDTO.setPrice(499.5);
        orderItemResponseDTO.setOrderId(1L);
        orderItemResponseDTO.setName("Mechanical Keyboard");
        orderItemResponseDTO.setDescription("RGB backlit mechanical keyboard with blue switches.");
        orderItemResponseDTO.setStock(200);

        orderItemResponseDTOList.add(orderItemResponseDTO);

        try {
            when(orderItemService.listItems()).thenReturn(orderItemResponseDTOList);
            ResponseEntity<List<OrderItemResponseDTO>> responseEntity=orderServiceController.listItems();
            assertEquals(200,responseEntity.getStatusCode().value());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testListItemsNegativeAssertReturnValue() {
        List<OrderItemResponseDTO> orderItemResponseDTOList=new ArrayList<>();
        try {
            when(orderItemService.listItems()).thenReturn(orderItemResponseDTOList);
            ResponseEntity<List<OrderItemResponseDTO>> responseEntity=orderServiceController.listItems();
            assertNull(responseEntity.getBody());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testListItemsNegativeAssertStatusCode() {
        List<OrderItemResponseDTO> orderItemResponseDTOList=new ArrayList<>();
        try {
            when(orderItemService.listItems()).thenReturn(orderItemResponseDTOList);
            ResponseEntity<List<OrderItemResponseDTO>> responseEntity=orderServiceController.listItems();
            assertEquals(400,responseEntity.getStatusCode().value());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testListItemsByProductPositiveAssertReturnValue() {
        List<OrderItemResponseDTO> orderItemResponseDTOList=new ArrayList<>();

        OrderItemResponseDTO orderItemResponseDTO=new OrderItemResponseDTO();
        orderItemResponseDTO.setId(1L);
        orderItemResponseDTO.setProductId(1L);
        orderItemResponseDTO.setQuantity(2);
        orderItemResponseDTO.setPrice(499.5);
        orderItemResponseDTO.setOrderId(1L);
        orderItemResponseDTO.setName("Mechanical Keyboard");
        orderItemResponseDTO.setDescription("RGB backlit mechanical keyboard with blue switches.");
        orderItemResponseDTO.setStock(200);

        orderItemResponseDTOList.add(orderItemResponseDTO);

        try {
            when(orderItemService.listItemsByProduct(any())).thenReturn(orderItemResponseDTOList);
            ResponseEntity<List<OrderItemResponseDTO>> responseEntity=orderServiceController.listItemsByProduct(1L);
            List<OrderItemResponseDTO> actualOrderItemResponseDTOList=responseEntity.getBody();
            assertTrue(actualOrderItemResponseDTOList.size()>0);
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testListItemsByProductPositiveAssertStatusCode() {
        List<OrderItemResponseDTO> orderItemResponseDTOList=new ArrayList<>();

        OrderItemResponseDTO orderItemResponseDTO=new OrderItemResponseDTO();
        orderItemResponseDTO.setId(1L);
        orderItemResponseDTO.setProductId(1L);
        orderItemResponseDTO.setQuantity(2);
        orderItemResponseDTO.setPrice(499.5);
        orderItemResponseDTO.setOrderId(1L);
        orderItemResponseDTO.setName("Mechanical Keyboard");
        orderItemResponseDTO.setDescription("RGB backlit mechanical keyboard with blue switches.");
        orderItemResponseDTO.setStock(200);

        orderItemResponseDTOList.add(orderItemResponseDTO);

        try {
            when(orderItemService.listItemsByProduct(any())).thenReturn(orderItemResponseDTOList);
            ResponseEntity<List<OrderItemResponseDTO>> responseEntity=orderServiceController.listItemsByProduct(1L);
            assertEquals(200,responseEntity.getStatusCode().value());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testListItemsByProductNegativeAssertReturnValue() {
        List<OrderItemResponseDTO> orderItemResponseDTOList=new ArrayList<>();
        try {
            when(orderItemService.listItemsByProduct(any())).thenReturn(orderItemResponseDTOList);
            ResponseEntity<List<OrderItemResponseDTO>> responseEntity=orderServiceController.listItemsByProduct(1L);
            assertNull(responseEntity.getBody());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testListItemsByProductNegativeAssertStatusCode() {
        List<OrderItemResponseDTO> orderItemResponseDTOList=new ArrayList<>();
        try {
            when(orderItemService.listItemsByProduct(any())).thenReturn(orderItemResponseDTOList);
            ResponseEntity<List<OrderItemResponseDTO>> responseEntity=orderServiceController.listItemsByProduct(1L);
            assertEquals(400,responseEntity.getStatusCode().value());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testListItemsByOrderPositiveAssertReturnValue() {
        List<OrderItemResponseDTO> orderItemResponseDTOList=new ArrayList<>();

        OrderItemResponseDTO orderItemResponseDTO=new OrderItemResponseDTO();
        orderItemResponseDTO.setId(1L);
        orderItemResponseDTO.setProductId(1L);
        orderItemResponseDTO.setQuantity(2);
        orderItemResponseDTO.setPrice(499.5);
        orderItemResponseDTO.setOrderId(1L);
        orderItemResponseDTO.setName("Mechanical Keyboard");
        orderItemResponseDTO.setDescription("RGB backlit mechanical keyboard with blue switches.");
        orderItemResponseDTO.setStock(200);

        orderItemResponseDTOList.add(orderItemResponseDTO);

        try {
            when(orderItemService.listItemsByOrder(1l)).thenReturn(orderItemResponseDTOList);
            ResponseEntity<List<OrderItemResponseDTO>> responseEntity=orderServiceController.listItemsByOrder(1L);
            List<OrderItemResponseDTO> actualOrderItemResponseDTOList=responseEntity.getBody();
            assertTrue(actualOrderItemResponseDTOList.size()>0);
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testListItemsByOrderPositiveAssertStatusCode() {
        List<OrderItemResponseDTO> orderItemResponseDTOList=new ArrayList<>();

        OrderItemResponseDTO orderItemResponseDTO=new OrderItemResponseDTO();
        orderItemResponseDTO.setId(1L);
        orderItemResponseDTO.setProductId(1L);
        orderItemResponseDTO.setQuantity(2);
        orderItemResponseDTO.setPrice(499.5);
        orderItemResponseDTO.setOrderId(1L);
        orderItemResponseDTO.setName("Mechanical Keyboard");
        orderItemResponseDTO.setDescription("RGB backlit mechanical keyboard with blue switches.");
        orderItemResponseDTO.setStock(200);

        orderItemResponseDTOList.add(orderItemResponseDTO);

        try {
            when(orderItemService.listItemsByOrder(1l)).thenReturn(orderItemResponseDTOList);
            ResponseEntity<List<OrderItemResponseDTO>> responseEntity=orderServiceController.listItemsByOrder(1L);
            assertEquals(200,responseEntity.getStatusCode().value());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testListItemsByOrderNegativeAssertReturnValue() {
        List<OrderItemResponseDTO> orderItemResponseDTOList=new ArrayList<>();
        try {
            when(orderItemService.listItemsByOrder(1l)).thenReturn(orderItemResponseDTOList);
            ResponseEntity<List<OrderItemResponseDTO>> responseEntity=orderServiceController.listItemsByOrder(1L);
            assertNull(responseEntity.getBody());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testListItemsByOrderNegativeAssertStatusCode() {
        List<OrderItemResponseDTO> orderItemResponseDTOList=new ArrayList<>();
        try {
            when(orderItemService.listItemsByOrder(1l)).thenReturn(orderItemResponseDTOList);
            ResponseEntity<List<OrderItemResponseDTO>> responseEntity=orderServiceController.listItemsByOrder(1L);
            assertEquals(400,responseEntity.getStatusCode().value());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testGetItemPositiveAssertReturnValue() {
        OrderItemResponseDTO orderItemResponseDTO=new OrderItemResponseDTO();
        orderItemResponseDTO.setId(1L);
        orderItemResponseDTO.setProductId(1L);
        orderItemResponseDTO.setQuantity(2);
        orderItemResponseDTO.setPrice(499.5);
        orderItemResponseDTO.setOrderId(1L);
        orderItemResponseDTO.setName("Mechanical Keyboard");
        orderItemResponseDTO.setDescription("RGB backlit mechanical keyboard with blue switches.");
        orderItemResponseDTO.setStock(200);

        try {
            when(orderItemService.getItem(any())).thenReturn(orderItemResponseDTO);
            ResponseEntity<OrderItemResponseDTO> responseEntity=orderServiceController.getItem(1L);
            OrderItemResponseDTO actualOrderItemResponseDTO=responseEntity.getBody();
            assertNotNull(actualOrderItemResponseDTO);
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testGetItemPositiveAssertStatusCode() {
        OrderItemResponseDTO orderItemResponseDTO=new OrderItemResponseDTO();
        orderItemResponseDTO.setId(1L);
        orderItemResponseDTO.setProductId(1L);
        orderItemResponseDTO.setQuantity(2);
        orderItemResponseDTO.setPrice(499.5);
        orderItemResponseDTO.setOrderId(1L);
        orderItemResponseDTO.setName("Mechanical Keyboard");
        orderItemResponseDTO.setDescription("RGB backlit mechanical keyboard with blue switches.");
        orderItemResponseDTO.setStock(200);

        try {
            when(orderItemService.getItem(any())).thenReturn(orderItemResponseDTO);
            ResponseEntity<OrderItemResponseDTO> responseEntity=orderServiceController.getItem(1L);
            assertEquals(200,responseEntity.getStatusCode().value());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testGetItemNegativeAssertReturnValue() {
        OrderItemResponseDTO orderItemResponseDTO=null;
        try {
            when(orderItemService.getItem(any())).thenReturn(orderItemResponseDTO);
            ResponseEntity<OrderItemResponseDTO> responseEntity=orderServiceController.getItem(1L);
            assertNull(responseEntity.getBody());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testGetItemNegativeAssertStatusCode() {
        OrderItemResponseDTO orderItemResponseDTO=null;
        try {
            when(orderItemService.getItem(any())).thenReturn(orderItemResponseDTO);
            ResponseEntity<OrderItemResponseDTO> responseEntity=orderServiceController.getItem(1L);
            assertEquals(400,responseEntity.getStatusCode().value());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testDeleteItemPositiveAssertReturnValue() {
        OrderItemResponseDTO orderItemResponseDTO=new OrderItemResponseDTO();
        orderItemResponseDTO.setId(1L);
        orderItemResponseDTO.setProductId(1L);
        orderItemResponseDTO.setQuantity(2);
        orderItemResponseDTO.setPrice(499.5);
        orderItemResponseDTO.setOrderId(1L);
        orderItemResponseDTO.setName("Mechanical Keyboard");
        orderItemResponseDTO.setDescription("RGB backlit mechanical keyboard with blue switches.");
        orderItemResponseDTO.setStock(200);

        try {
            when(orderItemService.deleteItem(any())).thenReturn("Item deleted with Id: " + orderItemResponseDTO.getId());
            ResponseEntity<String> responseEntity=orderServiceController.deleteItem(1L);
            String result=responseEntity.getBody();
            assertNotNull(result);
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testDeleteItemPositiveAssertStatusCode() {
        OrderItemResponseDTO orderItemResponseDTO=new OrderItemResponseDTO();
        orderItemResponseDTO.setId(1L);
        orderItemResponseDTO.setProductId(1L);
        orderItemResponseDTO.setQuantity(2);
        orderItemResponseDTO.setPrice(499.5);
        orderItemResponseDTO.setOrderId(1L);
        orderItemResponseDTO.setName("Mechanical Keyboard");
        orderItemResponseDTO.setDescription("RGB backlit mechanical keyboard with blue switches.");
        orderItemResponseDTO.setStock(200);

        try {
            when(orderItemService.deleteItem(any())).thenReturn("Item deleted with Id: " + orderItemResponseDTO.getId());
            ResponseEntity<String> responseEntity=orderServiceController.deleteItem(1L);
            assertEquals(200,responseEntity.getStatusCode().value());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testDeleteItemNegativeAssertReturnValue() {
        String result=null;
        try {
            when(orderItemService.deleteItem(any())).thenReturn(result);
            ResponseEntity<String> responseEntity=orderServiceController.deleteItem(1L);
            assertNull(responseEntity.getBody());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testDeleteItemNegativeAssertStatusCode() {
        String result=null;
        try {
            when(orderItemService.deleteItem(any())).thenReturn(result);
            ResponseEntity<String> responseEntity=orderServiceController.deleteItem(1L);
            assertEquals(400,responseEntity.getStatusCode().value());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testAddItemWhenItemIsValid() {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setId(1L);
        orderItemDTO.setProductId(1L);
        orderItemDTO.setQuantity(2);
//        orderItemDTO.setPrice(499.5);
        orderItemDTO.setOrderId(1L);

        validator.validate(orderItemDTO).stream().forEach((constraintViolation)->assertNull(constraintViolation));
    }

    @Test
    void testAddItemPositiveAssertReturnValue() {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setId(1L);
        orderItemDTO.setProductId(1L);
        orderItemDTO.setQuantity(2);
//        orderItemDTO.setPrice(499.5);
        orderItemDTO.setOrderId(1L);

        OrderItemResponseDTO orderItemResponseDTO=new OrderItemResponseDTO();
        orderItemResponseDTO.setId(1L);
        orderItemResponseDTO.setProductId(1L);
        orderItemResponseDTO.setQuantity(2);
        orderItemResponseDTO.setPrice(499.5);
        orderItemResponseDTO.setOrderId(1L);
        orderItemResponseDTO.setName("Mechanical Keyboard");
        orderItemResponseDTO.setDescription("RGB backlit mechanical keyboard with blue switches.");
        orderItemResponseDTO.setStock(200);

        try {
            when(orderItemService.addItem(any(OrderItemDTO.class))).thenReturn(orderItemResponseDTO);
            ResponseEntity<OrderItemResponseDTO> responseEntity=orderServiceController.addItem(orderItemDTO);
            OrderItemResponseDTO actualOrderItemResponseDTO=responseEntity.getBody();
            assertNotNull(actualOrderItemResponseDTO);
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testAddItemPositiveAssertStatusCode() {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setId(1L);
        orderItemDTO.setProductId(1L);
        orderItemDTO.setQuantity(2);
//        orderItemDTO.setPrice(499.5);
        orderItemDTO.setOrderId(1L);

        OrderItemResponseDTO orderItemResponseDTO=new OrderItemResponseDTO();
        orderItemResponseDTO.setId(1L);
        orderItemResponseDTO.setProductId(1L);
        orderItemResponseDTO.setQuantity(2);
        orderItemResponseDTO.setPrice(499.5);
        orderItemResponseDTO.setOrderId(1L);
        orderItemResponseDTO.setName("Mechanical Keyboard");
        orderItemResponseDTO.setDescription("RGB backlit mechanical keyboard with blue switches.");
        orderItemResponseDTO.setStock(200);

        try {
            when(orderItemService.addItem(any(OrderItemDTO.class))).thenReturn(orderItemResponseDTO);
            ResponseEntity<OrderItemResponseDTO> responseEntity=orderServiceController.addItem(orderItemDTO);
            assertEquals(201,responseEntity.getStatusCode().value());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testAddItemWhenOrderIsNotValid() {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setId(1L);
        orderItemDTO.setProductId(-1L);
        orderItemDTO.setQuantity(-2);
//        orderItemDTO.setPrice(-499.5);
        orderItemDTO.setOrderId(1L);

        validator.validate(orderItemDTO).stream().forEach((constraintViolation)->assertNotNull(constraintViolation));
    }

    @Test
    void testAddItemNegativeAssertReturnValue() {
        OrderItemDTO orderItemDTO=null;
        OrderItemResponseDTO orderItemResponseDTO=null;

        try {
            when(orderItemService.addItem(any(OrderItemDTO.class))).thenReturn(orderItemResponseDTO);
            ResponseEntity<OrderItemResponseDTO> responseEntity=orderServiceController.addItem(orderItemDTO);
            OrderItemResponseDTO actualOrderItemResponseDTO=responseEntity.getBody();
            assertNull(actualOrderItemResponseDTO);
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testAddItemNegativeAssertStatusCode() {
        OrderItemDTO orderItemDTO=null;
        OrderItemResponseDTO orderItemResponseDTO=null;

        try {
            when(orderItemService.addItem(any(OrderItemDTO.class))).thenReturn(orderItemResponseDTO);
            ResponseEntity<OrderItemResponseDTO> responseEntity=orderServiceController.addItem(orderItemDTO);
            assertEquals(400,responseEntity.getStatusCode().value());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testUpdateItemWhenItemIsValid() {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setId(1L);
        orderItemDTO.setProductId(1L);
        orderItemDTO.setQuantity(2);
//        orderItemDTO.setPrice(499.5);
        orderItemDTO.setOrderId(1L);

        validator.validate(orderItemDTO).stream().forEach((constraintViolation)->assertNull(constraintViolation));
    }

    @Test
    void testUpdateItemPositiveAssertReturnValue() {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setId(1L);
        orderItemDTO.setProductId(1L);
        orderItemDTO.setQuantity(2);
//        orderItemDTO.setPrice(499.5);
        orderItemDTO.setOrderId(1L);

        OrderItemResponseDTO orderItemResponseDTO=new OrderItemResponseDTO();
        orderItemResponseDTO.setId(1L);
        orderItemResponseDTO.setProductId(1L);
        orderItemResponseDTO.setQuantity(2);
        orderItemResponseDTO.setPrice(499.5);
        orderItemResponseDTO.setOrderId(1L);
        orderItemResponseDTO.setName("Mechanical Keyboard");
        orderItemResponseDTO.setDescription("RGB backlit mechanical keyboard with blue switches.");
        orderItemResponseDTO.setStock(200);

        try {
            when(orderItemService.updateItem(any(),any(OrderItemDTO.class))).thenReturn(orderItemResponseDTO);
            ResponseEntity<OrderItemResponseDTO> responseEntity=orderServiceController.updateItem(1l,orderItemDTO);
            OrderItemResponseDTO actualOrderItemResponseDTO=responseEntity.getBody();
            assertNotNull(actualOrderItemResponseDTO);
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testUpdateItemPositiveAssertStatusCode() {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setId(1L);
        orderItemDTO.setProductId(1L);
        orderItemDTO.setQuantity(2);
//        orderItemDTO.setPrice(499.5);
        orderItemDTO.setOrderId(1L);

        OrderItemResponseDTO orderItemResponseDTO=new OrderItemResponseDTO();
        orderItemResponseDTO.setId(1L);
        orderItemResponseDTO.setProductId(1L);
        orderItemResponseDTO.setQuantity(2);
        orderItemResponseDTO.setPrice(499.5);
        orderItemResponseDTO.setOrderId(1L);
        orderItemResponseDTO.setName("Mechanical Keyboard");
        orderItemResponseDTO.setDescription("RGB backlit mechanical keyboard with blue switches.");
        orderItemResponseDTO.setStock(200);

        try {
            when(orderItemService.updateItem(any(),any(OrderItemDTO.class))).thenReturn(orderItemResponseDTO);
            ResponseEntity<OrderItemResponseDTO> responseEntity=orderServiceController.updateItem(1l,orderItemDTO);
            assertEquals(202,responseEntity.getStatusCode().value());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testUpdateItemWhenOrderIsNotValid() {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setId(1L);
        orderItemDTO.setProductId(-1L);
        orderItemDTO.setQuantity(-2);
//        orderItemDTO.setPrice(-499.5);
        orderItemDTO.setOrderId(1L);

        validator.validate(orderItemDTO).stream().forEach((constraintViolation)->assertNotNull(constraintViolation));
    }

    @Test
    void testUpdateItemNegativeAssertReturnValue() {
        OrderItemDTO orderItemDTO=null;
        OrderItemResponseDTO orderItemResponseDTO=null;

        try {
            when(orderItemService.updateItem(any(),any(OrderItemDTO.class))).thenReturn(orderItemResponseDTO);
            ResponseEntity<OrderItemResponseDTO> responseEntity=orderServiceController.updateItem(1l,orderItemDTO);
            OrderItemResponseDTO actualOrderItemResponseDTO=responseEntity.getBody();
            assertNull(actualOrderItemResponseDTO);
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testUpdateItemNegativeAssertStatusCode() {
        OrderItemDTO orderItemDTO=null;
        OrderItemResponseDTO orderItemResponseDTO=null;

        try {
            when(orderItemService.updateItem(any(),any(OrderItemDTO.class))).thenReturn(orderItemResponseDTO);
            ResponseEntity<OrderItemResponseDTO> responseEntity=orderServiceController.updateItem(1l,orderItemDTO);
            assertEquals(400,responseEntity.getStatusCode().value());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testGetOrderPositiveAssertStatus() {
        OrderResponseDTO orderResponseDTO=new OrderResponseDTO();
        orderResponseDTO.setId(1L);
        orderResponseDTO.setUserId(1L);
        orderResponseDTO.setStatus("CREATED");
        orderResponseDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));
        orderResponseDTO.setUserName("Aman");
        orderResponseDTO.setEmail("Aman@example.com");

        try {
            when(orderService.getOrder(any())).thenReturn(orderResponseDTO);
            ResponseEntity<OrderResponseDTO> responseEntity=orderServiceController.getOrder(1L);
            assertEquals("CREATED",responseEntity.getBody().getStatus());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testGetOrderPositiveAssertUserId() {
        OrderResponseDTO orderResponseDTO=new OrderResponseDTO();
        orderResponseDTO.setId(1L);
        orderResponseDTO.setUserId(1L);
        orderResponseDTO.setStatus("CREATED");
        orderResponseDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));
        orderResponseDTO.setUserName("Aman");
        orderResponseDTO.setEmail("Aman@example.com");

        try {
            when(orderService.getOrder(any())).thenReturn(orderResponseDTO);
            ResponseEntity<OrderResponseDTO> responseEntity=orderServiceController.getOrder(1L);
            assertEquals(1L,responseEntity.getBody().getUserId());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testCreateOrderPositiveAssertStatus() {
        OrderDTO orderDTO=new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setUserId(1L);
        orderDTO.setStatus("CREATED");
        orderDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));

        OrderResponseDTO orderResponseDTO=new OrderResponseDTO();
        orderResponseDTO.setId(1L);
        orderResponseDTO.setUserId(1L);
        orderResponseDTO.setStatus("CREATED");
        orderResponseDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));
        orderResponseDTO.setUserName("Aman");
        orderResponseDTO.setEmail("Aman@example.com");

        try {
            when(orderService.createOrder(any(OrderDTO.class))).thenReturn(orderResponseDTO);
            ResponseEntity<OrderResponseDTO> responseEntity=orderServiceController.createOrder(orderDTO);
            assertEquals("CREATED",responseEntity.getBody().getStatus());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testListOrdersByUserPositiveAssertListSize() {
        List<OrderResponseDTO> orderResponseDTOList=new ArrayList<>();

        OrderResponseDTO orderResponseDTO1=new OrderResponseDTO();
        orderResponseDTO1.setId(1L);
        orderResponseDTO1.setUserId(1L);
        orderResponseDTO1.setStatus("CREATED");
        orderResponseDTO1.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));
        orderResponseDTO1.setUserName("Aman");
        orderResponseDTO1.setEmail("Aman@example.com");

        OrderResponseDTO orderResponseDTO2=new OrderResponseDTO();
        orderResponseDTO2.setId(2L);
        orderResponseDTO2.setUserId(1L);
        orderResponseDTO2.setStatus("PAID");
        orderResponseDTO2.setCreatedAt(LocalDateTime.of(2026,3,1,10,0,0));
        orderResponseDTO2.setUserName("Aman");
        orderResponseDTO2.setEmail("Aman@example.com");

        orderResponseDTOList.add(orderResponseDTO1);
        orderResponseDTOList.add(orderResponseDTO2);

        try {
            when(orderService.listOrdersByUser(any())).thenReturn(orderResponseDTOList);
            ResponseEntity<List<OrderResponseDTO>> responseEntity=orderServiceController.listOrdersByUser(1L);
            assertEquals(2,responseEntity.getBody().size());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testDeleteOrderPositiveAssertMessage() {
        try {
            when(orderService.deleteOrder(any())).thenReturn("Order deleted with Id: 1");
            ResponseEntity<String> responseEntity=orderServiceController.deleteOrder(1L);
            assertEquals("Order deleted with Id: 1",responseEntity.getBody());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testGetItemPositiveAssertProductId() {
        OrderItemResponseDTO orderItemResponseDTO=new OrderItemResponseDTO();
        orderItemResponseDTO.setId(1L);
        orderItemResponseDTO.setProductId(1L);
        orderItemResponseDTO.setQuantity(2);
        orderItemResponseDTO.setPrice(499.5);
        orderItemResponseDTO.setOrderId(1L);
        orderItemResponseDTO.setName("Mechanical Keyboard");
        orderItemResponseDTO.setDescription("RGB backlit mechanical keyboard with blue switches.");
        orderItemResponseDTO.setStock(200);

        try {
            when(orderItemService.getItem(any())).thenReturn(orderItemResponseDTO);
            ResponseEntity<OrderItemResponseDTO> responseEntity=orderServiceController.getItem(1L);
            assertEquals(1L,responseEntity.getBody().getProductId());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testGetItemPositiveAssertQuantity() {
        OrderItemResponseDTO orderItemResponseDTO=new OrderItemResponseDTO();
        orderItemResponseDTO.setId(1L);
        orderItemResponseDTO.setProductId(1L);
        orderItemResponseDTO.setQuantity(2);
        orderItemResponseDTO.setPrice(499.5);
        orderItemResponseDTO.setOrderId(1L);
        orderItemResponseDTO.setName("Mechanical Keyboard");
        orderItemResponseDTO.setDescription("RGB backlit mechanical keyboard with blue switches.");
        orderItemResponseDTO.setStock(200);

        try {
            when(orderItemService.getItem(any())).thenReturn(orderItemResponseDTO);
            ResponseEntity<OrderItemResponseDTO> responseEntity=orderServiceController.getItem(1L);
            assertEquals(2,responseEntity.getBody().getQuantity());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testDeleteItemPositiveAssertMessage() {
        try {
            when(orderItemService.deleteItem(any())).thenReturn("Item deleted with Id: 1");
            ResponseEntity<String> responseEntity=orderServiceController.deleteItem(1L);
            assertEquals("Item deleted with Id: 1",responseEntity.getBody());
        }catch(Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void testUpdateOrderStatusPositiveAssertUpdatedStatus() {
        OrderResponseDTO orderResponseDTO=new OrderResponseDTO();
        orderResponseDTO.setId(1L);
        orderResponseDTO.setUserId(1L);
        orderResponseDTO.setStatus("SHIPPED");
        orderResponseDTO.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));
        orderResponseDTO.setUserName("Aman");
        orderResponseDTO.setEmail("Aman@example.com");

        try {
            when(orderService.updateOrderStatus(any(),anyString())).thenReturn(orderResponseDTO);
            ResponseEntity<OrderResponseDTO> responseEntity=orderServiceController.updateOrderStatus(1L,"SHIPPED");
            assertEquals("SHIPPED",responseEntity.getBody().getStatus());
        }catch(Exception e) {
            assertTrue(false);
        }
    }
}
