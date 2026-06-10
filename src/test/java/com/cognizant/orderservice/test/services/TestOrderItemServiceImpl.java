package com.cognizant.orderservice.test.services;

import com.cognizant.orderservice.dtos.*;
import com.cognizant.orderservice.entities.Order;
import com.cognizant.orderservice.entities.OrderItem;
import com.cognizant.orderservice.exceptions.ResourceNotFoundException;
import com.cognizant.orderservice.feignclients.ProductFeignClient;
import com.cognizant.orderservice.repositories.OrderItemRepository;
import com.cognizant.orderservice.repositories.OrderRepository;
import com.cognizant.orderservice.services.OrderItemServiceImpl;
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

public class TestOrderItemServiceImpl {

    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductFeignClient productFeignClient;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private OrderItemServiceImpl orderItemServiceImpl;

    private Validator validator;

    private static final String KEYBOARD_NAME = "Mechanical Keyboard";
    private static final String KEYBOARD_DESC = "RGB backlit mechanical keyboard with blue switches.";
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 2, 1, 10, 0, 0);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @AfterEach
    void tearDown() {}

    // ── Builders ─────────────────────────────────────────────────────────────────

    private Order buildOrder(Long id) {
        Order order = new Order();
        order.setId(id);
        order.setUserId(1L);
        order.setStatus("CREATED");
        order.setCreatedAt(CREATED_AT);
        return order;
    }

    private OrderItem buildOrderItem(Long id, Long productId) {
        OrderItem item = new OrderItem();
        item.setId(id);
        item.setProductId(productId);
        item.setQuantity(2);
        item.setPrice(499.5);
        item.setOrder(buildOrder(1L));
        return item;
    }

    private ProductDTO buildProductDTO(Long id) {
        ProductDTO dto = new ProductDTO();
        dto.setId(id);
        dto.setName(KEYBOARD_NAME);
        dto.setDescription(KEYBOARD_DESC);
        dto.setPrice(5099);
        dto.setStock(200);
        return dto;
    }

    private OrderItemResponseDTO buildItemResponseDTO(Long id, Long productId) {
        OrderItemResponseDTO dto = new OrderItemResponseDTO();
        dto.setId(id);
        dto.setProductId(productId);
        dto.setQuantity(2);
        dto.setPrice(499.5);
        dto.setOrderId(1L);
        dto.setName(KEYBOARD_NAME);
        dto.setDescription(KEYBOARD_DESC);
        dto.setStock(200);
        return dto;
    }

    private void stubGetItem(OrderItemResponseDTO responseDTO) {
        when(orderItemRepository.findById(any())).thenReturn(Optional.of(buildOrderItem(1L, 1L)));
        when(productFeignClient.getProduct(any())).thenReturn(buildProductDTO(1L));
        when(modelMapper.map(any(OrderItem.class), eq(OrderItemResponseDTO.class))).thenReturn(responseDTO);
        when(modelMapper.map(any(ProductDTO.class), eq(OrderItemResponseDTO.class))).thenReturn(responseDTO);
    }

    // ── listItems ─────────────────────────────────────────────────────────────────

    @Test
    void testListItemsPositiveOneItemFound() {
        List<OrderItem> listMock = mock(List.class);
        when(orderItemRepository.findAll()).thenReturn(listMock);
        OrderItem itemMock = mock(OrderItem.class);
        when(listMock.stream()).thenReturn(Stream.of(itemMock));
        when(itemMock.getProductId()).thenReturn(1L);
        Order orderMock = mock(Order.class);
        when(itemMock.getOrder()).thenReturn(orderMock);
        when(orderMock.getId()).thenReturn(1L);
        ProductDTO productDTOMock = mock(ProductDTO.class);
        when(productFeignClient.getProduct(any())).thenReturn(productDTOMock);
        OrderItemResponseDTO responseMock = mock(OrderItemResponseDTO.class);
        when(modelMapper.map(any(OrderItem.class), eq(OrderItemResponseDTO.class))).thenReturn(responseMock);
        when(modelMapper.map(any(ProductDTO.class), eq(OrderItemResponseDTO.class))).thenReturn(responseMock);

        assertEquals(1, orderItemServiceImpl.listItems().size());
    }

    @Test
    void testListItemsPositiveMultipleItemsFound() {
        List<OrderItem> listMock = mock(List.class);
        when(orderItemRepository.findAll()).thenReturn(listMock);
        OrderItem m1 = mock(OrderItem.class);
        OrderItem m2 = mock(OrderItem.class);
        when(listMock.stream()).thenReturn(Stream.of(m1, m2));
        when(m1.getProductId()).thenReturn(1L);
        when(m2.getProductId()).thenReturn(2L);
        Order order1 = mock(Order.class);
        Order order2 = mock(Order.class);
        when(m1.getOrder()).thenReturn(order1);
        when(order1.getId()).thenReturn(1L);
        when(m2.getOrder()).thenReturn(order2);
        when(order2.getId()).thenReturn(2L);
        ProductDTO productMock = mock(ProductDTO.class);
        when(productFeignClient.getProduct(any())).thenReturn(productMock);
        OrderItemResponseDTO responseMock = mock(OrderItemResponseDTO.class);
        when(modelMapper.map(any(OrderItem.class), eq(OrderItemResponseDTO.class))).thenReturn(responseMock);
        when(modelMapper.map(any(ProductDTO.class), eq(OrderItemResponseDTO.class))).thenReturn(responseMock);

        assertTrue(orderItemServiceImpl.listItems().size() > 1);
    }

    @Test
    void testListItemsNegativeWhenProductIsNotFound() {
        OrderItem itemMock = mock(OrderItem.class);
        when(orderItemRepository.findAll()).thenReturn(List.of(itemMock));
        when(itemMock.getProductId()).thenReturn(1L);
        FeignException notFound = mock(FeignException.NotFound.class);
        when(notFound.getMessage()).thenReturn("Product not found with Id: 1");
        when(notFound.status()).thenReturn(404);
        when(productFeignClient.getProduct(any())).thenThrow(notFound);

        assertThatThrownBy(() -> orderItemServiceImpl.listItems())
                .isInstanceOf(FeignException.NotFound.class)
                .hasMessage("Product not found with Id: 1");
    }

    @Test
    void testListItemsNegativeWhenListIsEmpty() {
        when(orderItemRepository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> orderItemServiceImpl.listItems())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Item List is Empty");
    }

    // ── listItemsByProduct ────────────────────────────────────────────────────────

    @Test
    void testListItemsByProductPositiveOneItemFound() {
        ProductDTO productMock = mock(ProductDTO.class);
        when(productFeignClient.getProduct(any())).thenReturn(productMock);
        List<OrderItem> listMock = mock(List.class);
        when(orderItemRepository.findByProductId(any())).thenReturn(listMock);
        OrderItem itemMock = mock(OrderItem.class);
        when(listMock.stream()).thenReturn(Stream.of(itemMock));
        Order orderMock = mock(Order.class);
        when(itemMock.getOrder()).thenReturn(orderMock);
        when(orderMock.getId()).thenReturn(1L);
        OrderItemResponseDTO responseMock = mock(OrderItemResponseDTO.class);
        when(modelMapper.map(any(OrderItem.class), eq(OrderItemResponseDTO.class))).thenReturn(responseMock);
        when(modelMapper.map(any(ProductDTO.class), eq(OrderItemResponseDTO.class))).thenReturn(responseMock);

        assertEquals(1, orderItemServiceImpl.listItemsByProduct(1L).size());
    }

    @Test
    void testListItemsByProductPositiveMultipleItemFound() {
        ProductDTO productMock = mock(ProductDTO.class);
        when(productFeignClient.getProduct(any())).thenReturn(productMock);
        List<OrderItem> listMock = mock(List.class);
        when(orderItemRepository.findByProductId(any())).thenReturn(listMock);
        OrderItem m1 = mock(OrderItem.class);
        OrderItem m2 = mock(OrderItem.class);
        when(listMock.stream()).thenReturn(Stream.of(m1, m2));
        Order order1 = mock(Order.class);
        Order order2 = mock(Order.class);
        when(m1.getOrder()).thenReturn(order1);
        when(order1.getId()).thenReturn(1L);
        when(m2.getOrder()).thenReturn(order2);
        when(order2.getId()).thenReturn(2L);
        OrderItemResponseDTO responseMock = mock(OrderItemResponseDTO.class);
        when(modelMapper.map(any(OrderItem.class), eq(OrderItemResponseDTO.class))).thenReturn(responseMock);
        when(modelMapper.map(any(ProductDTO.class), eq(OrderItemResponseDTO.class))).thenReturn(responseMock);

        assertTrue(orderItemServiceImpl.listItemsByProduct(1L).size() > 1);
    }

    @Test
    void testListItemsByProductNegativeWhenProductIsNotFound() {
        FeignException notFound = mock(FeignException.NotFound.class);
        when(notFound.getMessage()).thenReturn("Product not found with Id: 1");
        when(notFound.status()).thenReturn(404);
        when(productFeignClient.getProduct(any())).thenThrow(notFound);

        assertThatThrownBy(() -> orderItemServiceImpl.listItemsByProduct(1L))
                .isInstanceOf(FeignException.NotFound.class)
                .hasMessage("Product not found with Id: 1");
    }

    @Test
    void testItemsByProductNegativeWhenListIsEmpty() {
        when(orderItemRepository.findByProductId(any())).thenReturn(List.of());

        assertThatThrownBy(() -> orderItemServiceImpl.listItemsByProduct(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Item List is Empty");
    }

    // ── listItemsByOrder ──────────────────────────────────────────────────────────

    @Test
    void testListItemsByOrderPositiveOneItemFound() {
        List<OrderItem> listMock = mock(List.class);
        when(orderItemRepository.findByOrderId(any())).thenReturn(listMock);
        OrderItem itemMock = mock(OrderItem.class);
        when(listMock.stream()).thenReturn(Stream.of(itemMock));
        when(itemMock.getProductId()).thenReturn(1L);
        Order orderMock = mock(Order.class);
        when(itemMock.getOrder()).thenReturn(orderMock);
        when(orderMock.getId()).thenReturn(1L);
        ProductDTO productMock = mock(ProductDTO.class);
        when(productFeignClient.getProduct(any())).thenReturn(productMock);
        OrderItemResponseDTO responseMock = mock(OrderItemResponseDTO.class);
        when(modelMapper.map(any(OrderItem.class), eq(OrderItemResponseDTO.class))).thenReturn(responseMock);
        when(modelMapper.map(any(ProductDTO.class), eq(OrderItemResponseDTO.class))).thenReturn(responseMock);

        assertEquals(1, orderItemServiceImpl.listItemsByOrder(1L).size());
    }

    @Test
    void testListItemsByOrderPositiveMultipleItemsFound() {
        List<OrderItem> listMock = mock(List.class);
        when(orderItemRepository.findByOrderId(any())).thenReturn(listMock);
        OrderItem m1 = mock(OrderItem.class);
        OrderItem m2 = mock(OrderItem.class);
        when(listMock.stream()).thenReturn(Stream.of(m1, m2));
        when(m1.getProductId()).thenReturn(1L);
        when(m2.getProductId()).thenReturn(2L);
        Order order1 = mock(Order.class);
        Order order2 = mock(Order.class);
        when(m1.getOrder()).thenReturn(order1);
        when(order1.getId()).thenReturn(1L);
        when(m2.getOrder()).thenReturn(order2);
        when(order2.getId()).thenReturn(2L);
        ProductDTO productMock = mock(ProductDTO.class);
        when(productFeignClient.getProduct(any())).thenReturn(productMock);
        OrderItemResponseDTO responseMock = mock(OrderItemResponseDTO.class);
        when(modelMapper.map(any(OrderItem.class), eq(OrderItemResponseDTO.class))).thenReturn(responseMock);
        when(modelMapper.map(any(ProductDTO.class), eq(OrderItemResponseDTO.class))).thenReturn(responseMock);

        assertTrue(orderItemServiceImpl.listItemsByOrder(1L).size() > 1);
    }

    @Test
    void testListItemsByOrderNegativeWhenProductIsNotFound() {
        OrderItem itemMock = mock(OrderItem.class);
        when(orderItemRepository.findByOrderId(any())).thenReturn(List.of(itemMock));
        when(itemMock.getProductId()).thenReturn(1L);
        FeignException notFound = mock(FeignException.NotFound.class);
        when(notFound.getMessage()).thenReturn("Product not found with Id: 1");
        when(notFound.status()).thenReturn(404);
        when(productFeignClient.getProduct(any())).thenThrow(notFound);

        assertThatThrownBy(() -> orderItemServiceImpl.listItemsByOrder(1L))
                .isInstanceOf(FeignException.NotFound.class)
                .hasMessage("Product not found with Id: 1");
    }

    @Test
    void testListItemsByOrderNegativeWhenListIsEmpty() {
        when(orderItemRepository.findByOrderId(any())).thenReturn(List.of());

        assertThatThrownBy(() -> orderItemServiceImpl.listItemsByOrder(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Item List is Empty");
    }

    // ── getItem ───────────────────────────────────────────────────────────────────

    @Test
    void testGetItemPositive() {
        OrderItemResponseDTO responseDTO = buildItemResponseDTO(1L, 1L);
        stubGetItem(responseDTO);

        assertNotNull(orderItemServiceImpl.getItem(1L));
    }

    @Test
    void testGetItemPositiveAssertProductId() {
        OrderItemResponseDTO responseDTO = buildItemResponseDTO(1L, 1L);
        stubGetItem(responseDTO);

        assertEquals(1L, orderItemServiceImpl.getItem(1L).getProductId());
    }

    @Test
    void testGetItemPositiveAssertQuantity() {
        OrderItemResponseDTO responseDTO = buildItemResponseDTO(1L, 1L);
        stubGetItem(responseDTO);

        assertEquals(2, orderItemServiceImpl.getItem(1L).getQuantity());
    }

    @Test
    void testGetItemPositiveAssertPrice() {
        OrderItemResponseDTO responseDTO = buildItemResponseDTO(1L, 1L);
        stubGetItem(responseDTO);

        assertEquals(499.5, orderItemServiceImpl.getItem(1L).getPrice());
    }

    @Test
    void testGetItemPositiveAssertOrderId() {
        OrderItemResponseDTO responseDTO = buildItemResponseDTO(1L, 1L);
        stubGetItem(responseDTO);

        assertEquals(1L, orderItemServiceImpl.getItem(1L).getOrderId());
    }

    @Test
    void testGetItemNegativeWhenProductIsNotFound() {
        when(orderItemRepository.findById(any())).thenReturn(Optional.of(buildOrderItem(1L, 1L)));
        FeignException notFound = mock(FeignException.NotFound.class);
        when(notFound.getMessage()).thenReturn("Product not found with Id: 1");
        when(notFound.status()).thenReturn(404);
        when(productFeignClient.getProduct(any())).thenThrow(notFound);

        assertThatThrownBy(() -> orderItemServiceImpl.getItem(1L))
                .isInstanceOf(FeignException.NotFound.class)
                .hasMessageContaining("Product not found with Id: 1");
    }

    @Test
    void testGetItemNegativeWhenItemIsNotFound() {
        when(orderItemRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderItemServiceImpl.getItem(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Item not found with Id: 1");
    }

    // ── deleteItem ────────────────────────────────────────────────────────────────

    @Test
    void testDeleteItemPositiveAssertMessage() {
        when(orderItemRepository.findById(any())).thenReturn(Optional.of(buildOrderItem(1L, 1L)));

        assertEquals("Item deleted with Id: 1", orderItemServiceImpl.deleteItem(1L));
    }

    @Test
    void testDeleteItemNegativeWhenItemIsNotFound() {
        when(orderItemRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderItemServiceImpl.deleteItem(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Item not found with Id: 1");
    }

    // ── addItem ───────────────────────────────────────────────────────────────────

    @Test
    void testAddItemPositive() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(11L);
        dto.setQuantity(2);
        dto.setOrderId(77L);

        Order order = buildOrder(77L);
        OrderItem mappedItem = buildOrderItem(null, 11L);
        OrderItem savedItem = buildOrderItem(555L, 11L);
        OrderItemResponseDTO responseDTO = buildItemResponseDTO(555L, 11L);
        responseDTO.setOrderId(555L);

        when(orderRepository.findById(eq(77L))).thenReturn(Optional.of(order));
        when(productFeignClient.getProduct(eq(11L))).thenReturn(buildProductDTO(11L));
        when(modelMapper.map(any(OrderItemDTO.class), eq(OrderItem.class))).thenReturn(mappedItem);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(savedItem);
        when(modelMapper.map(any(OrderItem.class), eq(OrderItemResponseDTO.class))).thenReturn(responseDTO);
        when(modelMapper.map(any(ProductDTO.class), eq(OrderItemResponseDTO.class))).thenReturn(responseDTO);

        OrderItemResponseDTO actual = orderItemServiceImpl.addItem(dto);
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(555L);
        assertThat(actual.getProductId()).isEqualTo(11L);
        assertThat(actual.getQuantity()).isEqualTo(2);
    }

    @Test
    void testAddItemPositiveAssertQuantity() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(11L);
        dto.setQuantity(3);
        dto.setOrderId(77L);

        Order order = buildOrder(77L);
        OrderItem mappedItem = buildOrderItem(null, 11L);
        mappedItem.setQuantity(3);
        OrderItem savedItem = buildOrderItem(555L, 11L);
        savedItem.setQuantity(3);
        OrderItemResponseDTO responseDTO = buildItemResponseDTO(555L, 11L);
        responseDTO.setQuantity(3);

        when(orderRepository.findById(eq(77L))).thenReturn(Optional.of(order));
        when(productFeignClient.getProduct(eq(11L))).thenReturn(buildProductDTO(11L));
        when(modelMapper.map(any(OrderItemDTO.class), eq(OrderItem.class))).thenReturn(mappedItem);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(savedItem);
        when(modelMapper.map(any(OrderItem.class), eq(OrderItemResponseDTO.class))).thenReturn(responseDTO);
        when(modelMapper.map(any(ProductDTO.class), eq(OrderItemResponseDTO.class))).thenReturn(responseDTO);

        assertEquals(3, orderItemServiceImpl.addItem(dto).getQuantity());
    }

    @Test
    void testAddItemNegativeWhenOrderNotFound() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(11L);
        dto.setQuantity(2);
        dto.setOrderId(77L);

        when(orderRepository.findById(eq(77L))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderItemServiceImpl.addItem(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order not found with Id: 77");
    }

    @Test
    void testAddItemNegativeWhenProductNotFound() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(11L);
        dto.setQuantity(2);
        dto.setOrderId(77L);

        when(orderRepository.findById(eq(77L))).thenReturn(Optional.of(buildOrder(77L)));
        FeignException notFound = mock(FeignException.NotFound.class);
        when(notFound.getMessage()).thenReturn("Product not found with Id: 11");
        when(notFound.status()).thenReturn(404);
        when(productFeignClient.getProduct(eq(11L))).thenThrow(notFound);

        assertThatThrownBy(() -> orderItemServiceImpl.addItem(dto))
                .isInstanceOf(FeignException.NotFound.class)
                .hasMessage("Product not found with Id: 11");
    }

    // ── updateItem ────────────────────────────────────────────────────────────────

    @Test
    void testUpdateItemPositive() {
        Long itemId = 555L;
        OrderItemDTO dto = new OrderItemDTO();
        dto.setOrderId(77L);
        dto.setProductId(11L);
        dto.setQuantity(5);

        Order order = buildOrder(77L);
        OrderItem existingItem = buildOrderItem(itemId, 11L);
        existingItem.setOrder(buildOrder(77L));
        OrderItem savedItem = buildOrderItem(itemId, 11L);
        savedItem.setQuantity(5);
        savedItem.setPrice(1499.0);
        savedItem.setOrder(order);
        OrderItemResponseDTO responseDTO = buildItemResponseDTO(itemId, 11L);
        responseDTO.setQuantity(5);
        responseDTO.setPrice(1499.0);
        responseDTO.setOrderId(77L);

        when(orderRepository.findById(eq(77L))).thenReturn(Optional.of(order));
        when(orderItemRepository.findById(eq(itemId))).thenReturn(Optional.of(existingItem));
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(savedItem);
        when(productFeignClient.getProduct(eq(11L))).thenReturn(buildProductDTO(11L));
        when(modelMapper.map(any(OrderItem.class), eq(OrderItemResponseDTO.class))).thenReturn(responseDTO);
        when(modelMapper.map(any(ProductDTO.class), eq(OrderItemResponseDTO.class))).thenReturn(responseDTO);

        OrderItemResponseDTO actual = orderItemServiceImpl.updateItem(itemId, dto);
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(itemId);
        assertThat(actual.getQuantity()).isEqualTo(5);
        assertThat(actual.getPrice()).isEqualTo(1499.0);
        assertThat(actual.getOrderId()).isEqualTo(77L);
    }

    @Test
    void testUpdateItemPositiveAssertUpdatedQuantity() {
        Long itemId = 555L;
        OrderItemDTO dto = new OrderItemDTO();
        dto.setOrderId(77L);
        dto.setProductId(11L);
        dto.setQuantity(10);

        Order order = buildOrder(77L);
        OrderItem existingItem = buildOrderItem(itemId, 11L);
        existingItem.setOrder(buildOrder(77L));
        OrderItem savedItem = buildOrderItem(itemId, 11L);
        savedItem.setQuantity(10);
        savedItem.setOrder(order);
        OrderItemResponseDTO responseDTO = buildItemResponseDTO(itemId, 11L);
        responseDTO.setQuantity(10);
        responseDTO.setOrderId(77L);

        when(orderRepository.findById(eq(77L))).thenReturn(Optional.of(order));
        when(orderItemRepository.findById(eq(itemId))).thenReturn(Optional.of(existingItem));
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(savedItem);
        when(productFeignClient.getProduct(eq(11L))).thenReturn(buildProductDTO(11L));
        when(modelMapper.map(any(OrderItem.class), eq(OrderItemResponseDTO.class))).thenReturn(responseDTO);
        when(modelMapper.map(any(ProductDTO.class), eq(OrderItemResponseDTO.class))).thenReturn(responseDTO);

        assertEquals(10, orderItemServiceImpl.updateItem(itemId, dto).getQuantity());
    }

    @Test
    void testUpdateItemNegativeWhenOrderNotFound() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setOrderId(77L);
        dto.setProductId(11L);
        dto.setQuantity(5);

        when(orderRepository.findById(eq(77L))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderItemServiceImpl.updateItem(555L, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order not found with Id: 77");
    }

    @Test
    void testUpdateItemNegativeWhenOrderItemNotFound() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setOrderId(77L);
        dto.setProductId(11L);
        dto.setQuantity(5);

        when(orderRepository.findById(eq(77L))).thenReturn(Optional.of(buildOrder(77L)));
        when(orderItemRepository.findById(eq(555L))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderItemServiceImpl.updateItem(555L, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order Item not found with Id: 555");
    }

    @Test
    void testUpdateItemNegativeWhenOrderIdMismatch() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setOrderId(99L);
        dto.setProductId(11L);
        dto.setQuantity(5);

        OrderItem existingItem = buildOrderItem(555L, 11L);
        existingItem.setOrder(buildOrder(77L));

        when(orderRepository.findById(eq(99L))).thenReturn(Optional.of(buildOrder(99L)));
        when(orderItemRepository.findById(eq(555L))).thenReturn(Optional.of(existingItem));

        assertThatThrownBy(() -> orderItemServiceImpl.updateItem(555L, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Please enter matching Order Id: 77");
    }

    @Test
    void testUpdateItemNegativeWhenProductNotFound() {
        Long itemId = 555L;
        OrderItemDTO dto = new OrderItemDTO();
        dto.setOrderId(77L);
        dto.setProductId(11L);
        dto.setQuantity(5);

        Order order = buildOrder(77L);
        OrderItem existingItem = buildOrderItem(itemId, 11L);
        existingItem.setOrder(buildOrder(77L));
        OrderItem savedItem = buildOrderItem(itemId, 11L);
        savedItem.setOrder(order);

        when(orderRepository.findById(eq(77L))).thenReturn(Optional.of(order));
        when(orderItemRepository.findById(eq(itemId))).thenReturn(Optional.of(existingItem));
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(savedItem);
        FeignException notFound = mock(FeignException.NotFound.class);
        when(notFound.getMessage()).thenReturn("Product not found with Id: 11");
        when(notFound.status()).thenReturn(404);
        when(productFeignClient.getProduct(eq(11L))).thenThrow(notFound);

        assertThatThrownBy(() -> orderItemServiceImpl.updateItem(itemId, dto))
                .isInstanceOf(FeignException.NotFound.class)
                .hasMessage("Product not found with Id: 11");
    }

    // ── Bean validation ───────────────────────────────────────────────────────────

    @Test
    void testOrderItemDTOValidation_Positive_NoViolations() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(10L);
        dto.setQuantity(1);
        dto.setOrderId(20L);

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void testOrderItemDTOValidation_ProductIdNull() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(null);
        dto.setQuantity(1);
        dto.setOrderId(1L);

        assertThat(validator.validate(dto)).extracting(v -> v.getMessage())
                .anyMatch(msg -> msg.contains("Product_Id is required"));
    }

    @Test
    void testOrderItemDTOValidation_ProductIdNegative() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(-5L);
        dto.setQuantity(1);
        dto.setOrderId(1L);

        assertThat(validator.validate(dto)).extracting(v -> v.getMessage())
                .anyMatch(msg -> msg.contains("Product_Id must be a positive number"));
    }

    @Test
    void testOrderItemDTOValidation_QuantityTooLow() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(10L);
        dto.setQuantity(0);
        dto.setOrderId(1L);

        assertThat(validator.validate(dto)).extracting(v -> v.getMessage())
                .anyMatch(msg -> msg.contains("Quantity must be at least 1"));
    }

    @Test
    void testOrderItemDTOValidation_OrderIdNull() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(10L);
        dto.setQuantity(2);
        dto.setOrderId(null);

        assertThat(validator.validate(dto)).extracting(v -> v.getMessage())
                .anyMatch(msg -> msg.contains("Order_Id is required"));
    }

    // ── Fallback methods – negative (empty list) ──────────────────────────────────

    @Test
    void testListItemsFallbackWhenListIsEmpty() {
        when(orderItemRepository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> orderItemServiceImpl.listItemsGetDefaultProduct(new RuntimeException("down")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Item List is Empty");
    }

    @Test
    void testListItemsByProductFallbackWhenListIsEmpty() {
        when(orderItemRepository.findByProductId(10L)).thenReturn(List.of());

        assertThatThrownBy(() -> orderItemServiceImpl.listItemsByProductGetDefaultProduct(10L, new RuntimeException("down")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Item List is Empty");
    }

    @Test
    void testListItemsByOrderFallbackWhenListIsEmpty() {
        when(orderItemRepository.findByOrderId(20L)).thenReturn(List.of());

        assertThatThrownBy(() -> orderItemServiceImpl.listItemsByOrderGetDefaultProduct(20L, new RuntimeException("down")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Item List is Empty");
    }

    @Test
    void testGetItemFallbackWhenItemNotFound() {
        when(orderItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderItemServiceImpl.getItemGetDefaultProduct(99L, new RuntimeException("down")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Item not found with Id: 99");
    }

    @Test
    void testUpdateItemFallbackOrderIdMismatch() {
        Long itemId = 1L;
        OrderItemDTO dto = new OrderItemDTO();
        dto.setOrderId(20L);
        dto.setProductId(10L);
        dto.setQuantity(1);

        OrderItem item = buildOrderItem(itemId, 10L);
        item.setOrder(buildOrder(10L));

        when(orderRepository.findById(20L)).thenReturn(Optional.of(buildOrder(20L)));
        when(orderItemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> orderItemServiceImpl.updateItemGetDefaultProduct(itemId, dto, new RuntimeException("down")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Please enter matching Order Id: 10");
    }

    @Test
    void testUpdateItemFallbackWhenOrderNotFound() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setOrderId(50L);

        when(orderRepository.findById(50L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderItemServiceImpl.updateItemGetDefaultProduct(1L, dto, new RuntimeException("down")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order not found with Id: 50");
    }

    @Test
    void testAddItemFallbackWhenOrderNotFound() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setOrderId(80L);
        dto.setProductId(10L);
        dto.setQuantity(1);

        when(orderRepository.findById(80L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderItemServiceImpl.addItemGetDefaultProduct(dto, new RuntimeException("down")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order not found with Id: 80");
    }

    // ── Fallback methods – positive (with items) ──────────────────────────────────

    @Test
    void testAddItemFallbackSuccess() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(11L);
        dto.setQuantity(2);
        dto.setOrderId(77L);

        Order order = buildOrder(77L);
        OrderItem mappedItem = buildOrderItem(null, 11L);
        OrderItem savedItem = buildOrderItem(555L, 11L);
        OrderItemResponseDTO responseDTO = buildItemResponseDTO(555L, 11L);

        when(orderRepository.findById(eq(77L))).thenReturn(Optional.of(order));
        when(modelMapper.map(any(OrderItemDTO.class), eq(OrderItem.class))).thenReturn(mappedItem);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(savedItem);
        when(modelMapper.map(any(OrderItem.class), eq(OrderItemResponseDTO.class))).thenReturn(responseDTO);
        when(modelMapper.map(any(ProductDTO.class), eq(OrderItemResponseDTO.class))).thenReturn(responseDTO);

        OrderItemResponseDTO actual = orderItemServiceImpl.addItemGetDefaultProduct(dto, new RuntimeException("down"));
        assertNotNull(actual);
        assertEquals(555L, actual.getId());
    }

    @Test
    void testGetItemFallbackSuccess() {
        OrderItem orderItem = buildOrderItem(1L, 1L);
        OrderItemResponseDTO responseDTO = buildItemResponseDTO(1L, 1L);

        when(orderItemRepository.findById(eq(1L))).thenReturn(Optional.of(orderItem));
        when(modelMapper.map(any(OrderItem.class), eq(OrderItemResponseDTO.class))).thenReturn(responseDTO);
        when(modelMapper.map(any(ProductDTO.class), eq(OrderItemResponseDTO.class))).thenReturn(responseDTO);

        OrderItemResponseDTO actual = orderItemServiceImpl.getItemGetDefaultProduct(1L, new RuntimeException("down"));
        assertNotNull(actual);
        assertEquals(KEYBOARD_NAME, actual.getName());
    }

    @Test
    void testListItemsFallbackWithItems() {
        OrderItem orderItem = buildOrderItem(1L, 5L);
        OrderItemResponseDTO responseDTO = buildItemResponseDTO(1L, 5L);

        when(orderItemRepository.findAll()).thenReturn(List.of(orderItem));
        when(modelMapper.map(any(OrderItem.class), eq(OrderItemResponseDTO.class))).thenReturn(responseDTO);
        when(modelMapper.map(any(ProductDTO.class), eq(OrderItemResponseDTO.class))).thenReturn(responseDTO);

        List<OrderItemResponseDTO> result = orderItemServiceImpl.listItemsGetDefaultProduct(new RuntimeException("down"));
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testListItemsByProductFallbackWithItems() {
        Long productId = 5L;
        OrderItem orderItem = buildOrderItem(1L, productId);
        OrderItemResponseDTO responseDTO = buildItemResponseDTO(1L, productId);

        when(orderItemRepository.findByProductId(productId)).thenReturn(List.of(orderItem));
        when(modelMapper.map(any(OrderItem.class), eq(OrderItemResponseDTO.class))).thenReturn(responseDTO);
        when(modelMapper.map(any(ProductDTO.class), eq(OrderItemResponseDTO.class))).thenReturn(responseDTO);

        List<OrderItemResponseDTO> result = orderItemServiceImpl.listItemsByProductGetDefaultProduct(productId, new RuntimeException("down"));
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testListItemsByOrderFallbackWithItems() {
        Long orderId = 10L;
        OrderItem orderItem = buildOrderItem(1L, 5L);
        orderItem.setOrder(buildOrder(orderId));
        OrderItemResponseDTO responseDTO = buildItemResponseDTO(1L, 5L);
        responseDTO.setOrderId(orderId);

        when(orderItemRepository.findByOrderId(orderId)).thenReturn(List.of(orderItem));
        when(modelMapper.map(any(OrderItem.class), eq(OrderItemResponseDTO.class))).thenReturn(responseDTO);
        when(modelMapper.map(any(ProductDTO.class), eq(OrderItemResponseDTO.class))).thenReturn(responseDTO);

        List<OrderItemResponseDTO> result = orderItemServiceImpl.listItemsByOrderGetDefaultProduct(orderId, new RuntimeException("down"));
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testUpdateItemFallbackSuccess() {
        Long itemId = 555L;
        OrderItemDTO dto = new OrderItemDTO();
        dto.setOrderId(77L);
        dto.setProductId(11L);
        dto.setQuantity(5);

        Order order = buildOrder(77L);
        OrderItem existingItem = buildOrderItem(itemId, 11L);
        existingItem.setOrder(buildOrder(77L));
        OrderItem savedItem = buildOrderItem(itemId, 11L);
        savedItem.setQuantity(5);
        savedItem.setOrder(order);
        OrderItemResponseDTO responseDTO = buildItemResponseDTO(itemId, 11L);
        responseDTO.setQuantity(5);

        when(orderRepository.findById(eq(77L))).thenReturn(Optional.of(order));
        when(orderItemRepository.findById(eq(itemId))).thenReturn(Optional.of(existingItem));
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(savedItem);
        when(modelMapper.map(any(OrderItem.class), eq(OrderItemResponseDTO.class))).thenReturn(responseDTO);
        when(modelMapper.map(any(ProductDTO.class), eq(OrderItemResponseDTO.class))).thenReturn(responseDTO);

        OrderItemResponseDTO actual = orderItemServiceImpl.updateItemGetDefaultProduct(itemId, dto, new RuntimeException("down"));
        assertNotNull(actual);
        assertEquals(5, actual.getQuantity());
    }
}
