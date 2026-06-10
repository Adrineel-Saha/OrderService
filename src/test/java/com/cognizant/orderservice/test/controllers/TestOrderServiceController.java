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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = OrderServiceApplication.class)
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

    private static final String USER_NAME = "Aman";
    private static final String USER_EMAIL = "Aman@example.com";
    private static final String KEYBOARD_NAME = "Mechanical Keyboard";
    private static final String KEYBOARD_DESC = "RGB backlit mechanical keyboard with blue switches.";
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 2, 1, 10, 0, 0);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    void tearDown() {}

    // ── Builders ─────────────────────────────────────────────────────────────────

    private OrderResponseDTO buildOrderResponse(String status) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(1L);
        dto.setUserId(1L);
        dto.setStatus(status);
        dto.setCreatedAt(CREATED_AT);
        dto.setUserName(USER_NAME);
        dto.setEmail(USER_EMAIL);
        return dto;
    }

    private List<OrderResponseDTO> buildOrderResponseList(String status) {
        List<OrderResponseDTO> list = new ArrayList<>();
        list.add(buildOrderResponse(status));
        return list;
    }

    private OrderItemResponseDTO buildItemResponse() {
        OrderItemResponseDTO dto = new OrderItemResponseDTO();
        dto.setId(1L);
        dto.setProductId(1L);
        dto.setQuantity(2);
        dto.setPrice(499.5);
        dto.setOrderId(1L);
        dto.setName(KEYBOARD_NAME);
        dto.setDescription(KEYBOARD_DESC);
        dto.setStock(200);
        return dto;
    }

    private List<OrderItemResponseDTO> buildItemResponseList() {
        List<OrderItemResponseDTO> list = new ArrayList<>();
        list.add(buildItemResponse());
        return list;
    }

    private OrderDTO buildOrderDTO() {
        OrderDTO dto = new OrderDTO();
        dto.setId(1L);
        dto.setUserId(1L);
        dto.setStatus("CREATED");
        dto.setCreatedAt(CREATED_AT);
        return dto;
    }

    private OrderItemDTO buildItemDTO() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(1L);
        dto.setProductId(1L);
        dto.setQuantity(2);
        dto.setOrderId(1L);
        return dto;
    }

    // ── listOrders ────────────────────────────────────────────────────────────────

    @Test
    void testListOrdersPositiveAssertReturnValue() {
        when(orderService.listOrders()).thenReturn(buildOrderResponseList("CREATED"));
        assertFalse(orderServiceController.listOrders().getBody().isEmpty());
    }

    @Test
    void testListOrdersPositiveAssertStatusCode() {
        when(orderService.listOrders()).thenReturn(buildOrderResponseList("CREATED"));
        assertEquals(200, orderServiceController.listOrders().getStatusCode().value());
    }

    @Test
    void testListOrdersPositiveAssertListSize() {
        List<OrderResponseDTO> list = new ArrayList<>();
        list.add(buildOrderResponse("CREATED"));
        list.add(buildOrderResponse("PAID"));
        when(orderService.listOrders()).thenReturn(list);
        assertEquals(2, orderServiceController.listOrders().getBody().size());
    }

    @Test
    void testListOrdersPositiveAssertEmail() {
        when(orderService.listOrders()).thenReturn(buildOrderResponseList("CREATED"));
        assertEquals(USER_EMAIL, orderServiceController.listOrders().getBody().get(0).getEmail());
    }

    @Test
    void testListOrdersNegativeAssertReturnValue() {
        when(orderService.listOrders()).thenReturn(new ArrayList<>());
        assertNull(orderServiceController.listOrders().getBody());
    }

    @Test
    void testListOrdersNegativeAssertStatusCode() {
        when(orderService.listOrders()).thenReturn(new ArrayList<>());
        assertEquals(400, orderServiceController.listOrders().getStatusCode().value());
    }

    // ── listOrdersByUser ──────────────────────────────────────────────────────────

    @Test
    void testListOrdersByUserPositiveAssertReturnValue() {
        when(orderService.listOrdersByUser(any())).thenReturn(buildOrderResponseList("CREATED"));
        assertFalse(orderServiceController.listOrdersByUser(1L).getBody().isEmpty());
    }

    @Test
    void testListOrdersByUserPositiveAssertStatusCode() {
        when(orderService.listOrdersByUser(any())).thenReturn(buildOrderResponseList("CREATED"));
        assertEquals(200, orderServiceController.listOrdersByUser(1L).getStatusCode().value());
    }

    @Test
    void testListOrdersByUserPositiveAssertListSize() {
        List<OrderResponseDTO> list = new ArrayList<>();
        list.add(buildOrderResponse("CREATED"));
        list.add(buildOrderResponse("PAID"));
        when(orderService.listOrdersByUser(any())).thenReturn(list);
        assertEquals(2, orderServiceController.listOrdersByUser(1L).getBody().size());
    }

    @Test
    void testListOrdersByUserPositiveAssertEmail() {
        when(orderService.listOrdersByUser(any())).thenReturn(buildOrderResponseList("CREATED"));
        assertEquals(USER_EMAIL, orderServiceController.listOrdersByUser(1L).getBody().get(0).getEmail());
    }

    @Test
    void testListOrdersByUserNegativeAssertReturnValue() {
        when(orderService.listOrdersByUser(any())).thenReturn(new ArrayList<>());
        assertNull(orderServiceController.listOrdersByUser(1L).getBody());
    }

    @Test
    void testListOrdersByUserNegativeAssertStatusCode() {
        when(orderService.listOrdersByUser(any())).thenReturn(new ArrayList<>());
        assertEquals(400, orderServiceController.listOrdersByUser(1L).getStatusCode().value());
    }

    // ── getOrder ──────────────────────────────────────────────────────────────────

    @Test
    void testGetOrderPositiveAssertReturnValue() {
        when(orderService.getOrder(any())).thenReturn(buildOrderResponse("CREATED"));
        assertNotNull(orderServiceController.getOrder(1L).getBody());
    }

    @Test
    void testGetOrderPositiveAssertStatusCode() {
        when(orderService.getOrder(any())).thenReturn(buildOrderResponse("CREATED"));
        assertEquals(200, orderServiceController.getOrder(1L).getStatusCode().value());
    }

    @Test
    void testGetOrderPositiveAssertStatus() {
        when(orderService.getOrder(any())).thenReturn(buildOrderResponse("CREATED"));
        assertEquals("CREATED", orderServiceController.getOrder(1L).getBody().getStatus());
    }

    @Test
    void testGetOrderPositiveAssertUserId() {
        when(orderService.getOrder(any())).thenReturn(buildOrderResponse("CREATED"));
        assertEquals(1L, orderServiceController.getOrder(1L).getBody().getUserId());
    }

    @Test
    void testGetOrderPositiveAssertEmail() {
        when(orderService.getOrder(any())).thenReturn(buildOrderResponse("CREATED"));
        assertEquals(USER_EMAIL, orderServiceController.getOrder(1L).getBody().getEmail());
    }

    @Test
    void testGetOrderPositiveAssertUserName() {
        when(orderService.getOrder(any())).thenReturn(buildOrderResponse("CREATED"));
        assertEquals(USER_NAME, orderServiceController.getOrder(1L).getBody().getUserName());
    }

    @Test
    void testGetOrderNegativeAssertReturnValue() {
        when(orderService.getOrder(any())).thenReturn(null);
        assertNull(orderServiceController.getOrder(1L).getBody());
    }

    @Test
    void testGetOrderNegativeAssertStatusCode() {
        when(orderService.getOrder(any())).thenReturn(null);
        assertEquals(400, orderServiceController.getOrder(1L).getStatusCode().value());
    }

    // ── createOrder ───────────────────────────────────────────────────────────────

    @Test
    void testCreateOrderWhenOrderIsValid() {
        validator.validate(buildOrderDTO()).forEach(v -> assertNull(v));
    }

    @Test
    void testCreateOrderPositiveAssertReturnValue() {
        when(orderService.createOrder(any(OrderDTO.class))).thenReturn(buildOrderResponse("CREATED"));
        assertNotNull(orderServiceController.createOrder(buildOrderDTO()).getBody());
    }

    @Test
    void testCreateOrderPositiveAssertStatusCode() {
        when(orderService.createOrder(any(OrderDTO.class))).thenReturn(buildOrderResponse("CREATED"));
        assertEquals(201, orderServiceController.createOrder(buildOrderDTO()).getStatusCode().value());
    }

    @Test
    void testCreateOrderPositiveAssertStatus() {
        when(orderService.createOrder(any(OrderDTO.class))).thenReturn(buildOrderResponse("CREATED"));
        assertEquals("CREATED", orderServiceController.createOrder(buildOrderDTO()).getBody().getStatus());
    }

    @Test
    void testCreateOrderPositiveAssertEmail() {
        when(orderService.createOrder(any(OrderDTO.class))).thenReturn(buildOrderResponse("CREATED"));
        assertEquals(USER_EMAIL, orderServiceController.createOrder(buildOrderDTO()).getBody().getEmail());
    }

    @Test
    void testCreateOrderPositiveAssertUserName() {
        when(orderService.createOrder(any(OrderDTO.class))).thenReturn(buildOrderResponse("CREATED"));
        assertEquals(USER_NAME, orderServiceController.createOrder(buildOrderDTO()).getBody().getUserName());
    }

    @Test
    void testCreateOrderWhenOrderIsNotValid() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setUserId(-1L);
        orderDTO.setStatus("INVALID_STATUS");
        orderDTO.setCreatedAt(CREATED_AT);
        validator.validate(orderDTO).forEach(v -> assertNotNull(v));
    }

    @Test
    void testCreateOrderNegativeAssertReturnValue() {
        when(orderService.createOrder(any(OrderDTO.class))).thenReturn(null);
        assertNull(orderServiceController.createOrder(null).getBody());
    }

    @Test
    void testCreateOrderNegativeAssertStatusCode() {
        when(orderService.createOrder(any(OrderDTO.class))).thenReturn(null);
        assertEquals(400, orderServiceController.createOrder(null).getStatusCode().value());
    }

    // ── updateOrderStatus ─────────────────────────────────────────────────────────

    @Test
    void testUpdateOrderStatusWhenOrderStatusIsValid() {
        assertTrue(validator.validateValue(OrderDTO.class, "status", "SHIPPED").isEmpty());
    }

    @Test
    void testUpdateOrderStatusWhenOrderStatusIsNotValid() {
        var violations = validator.validateValue(OrderDTO.class, "status", "INVALID_STATUS");
        assertFalse(violations.isEmpty());
        violations.forEach(v -> {
            assertEquals("status", v.getPropertyPath().toString());
            assertEquals("Status must be one of: CREATED, PAID, SHIPPED, CANCELLED", v.getMessage());
        });
    }

    @Test
    void testUpdateOrderStatusPositiveAssertReturnValue() {
        when(orderService.updateOrderStatus(any(), anyString())).thenReturn(buildOrderResponse("SHIPPED"));
        assertNotNull(orderServiceController.updateOrderStatus(1L, "SHIPPED").getBody());
    }

    @Test
    void testUpdateOrderStatusPositiveAssertStatusCode() {
        when(orderService.updateOrderStatus(any(), anyString())).thenReturn(buildOrderResponse("SHIPPED"));
        assertEquals(202, orderServiceController.updateOrderStatus(1L, "SHIPPED").getStatusCode().value());
    }

    @Test
    void testUpdateOrderStatusPositiveAssertUpdatedStatus() {
        OrderResponseDTO responseDTO = buildOrderResponse("SHIPPED");
        when(orderService.updateOrderStatus(any(), anyString())).thenReturn(responseDTO);
        assertEquals("SHIPPED", orderServiceController.updateOrderStatus(1L, "SHIPPED").getBody().getStatus());
    }

    @Test
    void testUpdateOrderStatusPositiveAssertUserName() {
        when(orderService.updateOrderStatus(any(), anyString())).thenReturn(buildOrderResponse("SHIPPED"));
        assertEquals(USER_NAME, orderServiceController.updateOrderStatus(1L, "SHIPPED").getBody().getUserName());
    }

    @Test
    void testUpdateOrderStatusNegativeAssertReturnValue() {
        when(orderService.updateOrderStatus(any(), anyString())).thenReturn(null);
        assertNull(orderServiceController.updateOrderStatus(1L, "INVALID").getBody());
    }

    @Test
    void testUpdateOrderStatusNegativeAssertStatusCode() {
        when(orderService.updateOrderStatus(any(), anyString())).thenReturn(null);
        assertEquals(400, orderServiceController.updateOrderStatus(1L, "INVALID").getStatusCode().value());
    }

    // ── deleteOrder ───────────────────────────────────────────────────────────────

    @Test
    void testDeleteOrderPositiveAssertReturnValue() {
        when(orderService.deleteOrder(any())).thenReturn("Order deleted with Id: 1");
        assertNotNull(orderServiceController.deleteOrder(1L).getBody());
    }

    @Test
    void testDeleteOrderPositiveAssertStatusCode() {
        when(orderService.deleteOrder(any())).thenReturn("Order deleted with Id: 1");
        assertEquals(200, orderServiceController.deleteOrder(1L).getStatusCode().value());
    }

    @Test
    void testDeleteOrderPositiveAssertMessage() {
        when(orderService.deleteOrder(any())).thenReturn("Order deleted with Id: 1");
        assertEquals("Order deleted with Id: 1", orderServiceController.deleteOrder(1L).getBody());
    }

    @Test
    void testDeleteOrderNegativeAssertReturnValue() {
        when(orderService.deleteOrder(any())).thenReturn(null);
        assertNull(orderServiceController.deleteOrder(1L).getBody());
    }

    @Test
    void testDeleteOrderNegativeAssertStatusCode() {
        when(orderService.deleteOrder(any())).thenReturn(null);
        assertEquals(400, orderServiceController.deleteOrder(1L).getStatusCode().value());
    }

    // ── listItems ─────────────────────────────────────────────────────────────────

    @Test
    void testListItemsPositiveAssertReturnValue() {
        when(orderItemService.listItems()).thenReturn(buildItemResponseList());
        assertFalse(orderServiceController.listItems().getBody().isEmpty());
    }

    @Test
    void testListItemsPositiveAssertStatusCode() {
        when(orderItemService.listItems()).thenReturn(buildItemResponseList());
        assertEquals(200, orderServiceController.listItems().getStatusCode().value());
    }

    @Test
    void testListItemsPositiveAssertListSize() {
        List<OrderItemResponseDTO> list = new ArrayList<>();
        list.add(buildItemResponse());
        list.add(buildItemResponse());
        when(orderItemService.listItems()).thenReturn(list);
        assertEquals(2, orderServiceController.listItems().getBody().size());
    }

    @Test
    void testListItemsNegativeAssertReturnValue() {
        when(orderItemService.listItems()).thenReturn(new ArrayList<>());
        assertNull(orderServiceController.listItems().getBody());
    }

    @Test
    void testListItemsNegativeAssertStatusCode() {
        when(orderItemService.listItems()).thenReturn(new ArrayList<>());
        assertEquals(400, orderServiceController.listItems().getStatusCode().value());
    }

    // ── listItemsByProduct ────────────────────────────────────────────────────────

    @Test
    void testListItemsByProductPositiveAssertReturnValue() {
        when(orderItemService.listItemsByProduct(any())).thenReturn(buildItemResponseList());
        assertFalse(orderServiceController.listItemsByProduct(1L).getBody().isEmpty());
    }

    @Test
    void testListItemsByProductPositiveAssertStatusCode() {
        when(orderItemService.listItemsByProduct(any())).thenReturn(buildItemResponseList());
        assertEquals(200, orderServiceController.listItemsByProduct(1L).getStatusCode().value());
    }

    @Test
    void testListItemsByProductPositiveAssertListSize() {
        when(orderItemService.listItemsByProduct(any())).thenReturn(buildItemResponseList());
        assertEquals(1, orderServiceController.listItemsByProduct(1L).getBody().size());
    }

    @Test
    void testListItemsByProductNegativeAssertReturnValue() {
        when(orderItemService.listItemsByProduct(any())).thenReturn(new ArrayList<>());
        assertNull(orderServiceController.listItemsByProduct(1L).getBody());
    }

    @Test
    void testListItemsByProductNegativeAssertStatusCode() {
        when(orderItemService.listItemsByProduct(any())).thenReturn(new ArrayList<>());
        assertEquals(400, orderServiceController.listItemsByProduct(1L).getStatusCode().value());
    }

    // ── listItemsByOrder ──────────────────────────────────────────────────────────

    @Test
    void testListItemsByOrderPositiveAssertReturnValue() {
        when(orderItemService.listItemsByOrder(1L)).thenReturn(buildItemResponseList());
        assertFalse(orderServiceController.listItemsByOrder(1L).getBody().isEmpty());
    }

    @Test
    void testListItemsByOrderPositiveAssertStatusCode() {
        when(orderItemService.listItemsByOrder(1L)).thenReturn(buildItemResponseList());
        assertEquals(200, orderServiceController.listItemsByOrder(1L).getStatusCode().value());
    }

    @Test
    void testListItemsByOrderPositiveAssertListSize() {
        List<OrderItemResponseDTO> list = new ArrayList<>();
        list.add(buildItemResponse());
        list.add(buildItemResponse());
        when(orderItemService.listItemsByOrder(1L)).thenReturn(list);
        assertEquals(2, orderServiceController.listItemsByOrder(1L).getBody().size());
    }

    @Test
    void testListItemsByOrderNegativeAssertReturnValue() {
        when(orderItemService.listItemsByOrder(1L)).thenReturn(new ArrayList<>());
        assertNull(orderServiceController.listItemsByOrder(1L).getBody());
    }

    @Test
    void testListItemsByOrderNegativeAssertStatusCode() {
        when(orderItemService.listItemsByOrder(1L)).thenReturn(new ArrayList<>());
        assertEquals(400, orderServiceController.listItemsByOrder(1L).getStatusCode().value());
    }

    // ── getItem ───────────────────────────────────────────────────────────────────

    @Test
    void testGetItemPositiveAssertReturnValue() {
        when(orderItemService.getItem(any())).thenReturn(buildItemResponse());
        assertNotNull(orderServiceController.getItem(1L).getBody());
    }

    @Test
    void testGetItemPositiveAssertStatusCode() {
        when(orderItemService.getItem(any())).thenReturn(buildItemResponse());
        assertEquals(200, orderServiceController.getItem(1L).getStatusCode().value());
    }

    @Test
    void testGetItemPositiveAssertProductId() {
        when(orderItemService.getItem(any())).thenReturn(buildItemResponse());
        assertEquals(1L, orderServiceController.getItem(1L).getBody().getProductId());
    }

    @Test
    void testGetItemPositiveAssertQuantity() {
        when(orderItemService.getItem(any())).thenReturn(buildItemResponse());
        assertEquals(2, orderServiceController.getItem(1L).getBody().getQuantity());
    }

    @Test
    void testGetItemPositiveAssertPrice() {
        when(orderItemService.getItem(any())).thenReturn(buildItemResponse());
        assertEquals(499.5, orderServiceController.getItem(1L).getBody().getPrice());
    }

    @Test
    void testGetItemPositiveAssertOrderId() {
        OrderItemResponseDTO dto = buildItemResponse();
        dto.setOrderId(5L);
        when(orderItemService.getItem(any())).thenReturn(dto);
        assertEquals(5L, orderServiceController.getItem(1L).getBody().getOrderId());
    }

    @Test
    void testGetItemNegativeAssertReturnValue() {
        when(orderItemService.getItem(any())).thenReturn(null);
        assertNull(orderServiceController.getItem(1L).getBody());
    }

    @Test
    void testGetItemNegativeAssertStatusCode() {
        when(orderItemService.getItem(any())).thenReturn(null);
        assertEquals(400, orderServiceController.getItem(1L).getStatusCode().value());
    }

    // ── addItem ───────────────────────────────────────────────────────────────────

    @Test
    void testAddItemWhenItemIsValid() {
        validator.validate(buildItemDTO()).forEach(v -> assertNull(v));
    }

    @Test
    void testAddItemPositiveAssertReturnValue() {
        when(orderItemService.addItem(any(OrderItemDTO.class))).thenReturn(buildItemResponse());
        assertNotNull(orderServiceController.addItem(buildItemDTO()).getBody());
    }

    @Test
    void testAddItemPositiveAssertStatusCode() {
        when(orderItemService.addItem(any(OrderItemDTO.class))).thenReturn(buildItemResponse());
        assertEquals(201, orderServiceController.addItem(buildItemDTO()).getStatusCode().value());
    }

    @Test
    void testAddItemPositiveAssertProductId() {
        OrderItemResponseDTO dto = buildItemResponse();
        dto.setProductId(5L);
        when(orderItemService.addItem(any(OrderItemDTO.class))).thenReturn(dto);
        assertEquals(5L, orderServiceController.addItem(buildItemDTO()).getBody().getProductId());
    }

    @Test
    void testAddItemPositiveAssertPrice() {
        when(orderItemService.addItem(any(OrderItemDTO.class))).thenReturn(buildItemResponse());
        assertEquals(499.5, orderServiceController.addItem(buildItemDTO()).getBody().getPrice());
    }

    @Test
    void testAddItemWhenOrderIsNotValid() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(-1L);
        dto.setQuantity(-2);
        dto.setOrderId(1L);
        validator.validate(dto).forEach(v -> assertNotNull(v));
    }

    @Test
    void testAddItemNegativeAssertReturnValue() {
        when(orderItemService.addItem(any(OrderItemDTO.class))).thenReturn(null);
        assertNull(orderServiceController.addItem(null).getBody());
    }

    @Test
    void testAddItemNegativeAssertStatusCode() {
        when(orderItemService.addItem(any(OrderItemDTO.class))).thenReturn(null);
        assertEquals(400, orderServiceController.addItem(null).getStatusCode().value());
    }

    // ── updateItem ────────────────────────────────────────────────────────────────

    @Test
    void testUpdateItemWhenItemIsValid() {
        validator.validate(buildItemDTO()).forEach(v -> assertNull(v));
    }

    @Test
    void testUpdateItemPositiveAssertReturnValue() {
        when(orderItemService.updateItem(any(), any(OrderItemDTO.class))).thenReturn(buildItemResponse());
        assertNotNull(orderServiceController.updateItem(1L, buildItemDTO()).getBody());
    }

    @Test
    void testUpdateItemPositiveAssertStatusCode() {
        when(orderItemService.updateItem(any(), any(OrderItemDTO.class))).thenReturn(buildItemResponse());
        assertEquals(202, orderServiceController.updateItem(1L, buildItemDTO()).getStatusCode().value());
    }

    @Test
    void testUpdateItemPositiveAssertProductId() {
        OrderItemResponseDTO dto = buildItemResponse();
        dto.setProductId(7L);
        when(orderItemService.updateItem(any(), any(OrderItemDTO.class))).thenReturn(dto);
        assertEquals(7L, orderServiceController.updateItem(1L, buildItemDTO()).getBody().getProductId());
    }

    @Test
    void testUpdateItemPositiveAssertPrice() {
        OrderItemResponseDTO dto = buildItemResponse();
        dto.setPrice(1499.0);
        when(orderItemService.updateItem(any(), any(OrderItemDTO.class))).thenReturn(dto);
        assertEquals(1499.0, orderServiceController.updateItem(1L, buildItemDTO()).getBody().getPrice());
    }

    @Test
    void testUpdateItemWhenOrderIsNotValid() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(-1L);
        dto.setQuantity(-2);
        dto.setOrderId(1L);
        validator.validate(dto).forEach(v -> assertNotNull(v));
    }

    @Test
    void testUpdateItemNegativeAssertReturnValue() {
        when(orderItemService.updateItem(any(), any(OrderItemDTO.class))).thenReturn(null);
        assertNull(orderServiceController.updateItem(1L, null).getBody());
    }

    @Test
    void testUpdateItemNegativeAssertStatusCode() {
        when(orderItemService.updateItem(any(), any(OrderItemDTO.class))).thenReturn(null);
        assertEquals(400, orderServiceController.updateItem(1L, null).getStatusCode().value());
    }

    // ── deleteItem ────────────────────────────────────────────────────────────────

    @Test
    void testDeleteItemPositiveAssertReturnValue() {
        when(orderItemService.deleteItem(any())).thenReturn("Item deleted with Id: 1");
        assertNotNull(orderServiceController.deleteItem(1L).getBody());
    }

    @Test
    void testDeleteItemPositiveAssertStatusCode() {
        when(orderItemService.deleteItem(any())).thenReturn("Item deleted with Id: 1");
        assertEquals(200, orderServiceController.deleteItem(1L).getStatusCode().value());
    }

    @Test
    void testDeleteItemPositiveAssertMessage() {
        when(orderItemService.deleteItem(any())).thenReturn("Item deleted with Id: 1");
        assertEquals("Item deleted with Id: 1", orderServiceController.deleteItem(1L).getBody());
    }

    @Test
    void testDeleteItemNegativeAssertReturnValue() {
        when(orderItemService.deleteItem(any())).thenReturn(null);
        assertNull(orderServiceController.deleteItem(1L).getBody());
    }

    @Test
    void testDeleteItemNegativeAssertStatusCode() {
        when(orderItemService.deleteItem(any())).thenReturn(null);
        assertEquals(400, orderServiceController.deleteItem(1L).getStatusCode().value());
    }
}
