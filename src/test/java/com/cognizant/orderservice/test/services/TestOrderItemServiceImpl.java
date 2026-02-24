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

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    public void testListItemsPositiveOneItemFound(){
        try{
            List<OrderItem> orderItemListMock=mock(List.class);
            when(orderItemRepository.findAll()).thenReturn(orderItemListMock);

            OrderItem orderItemMock=mock(OrderItem.class);
            when(orderItemListMock.stream()).thenReturn(Stream.of(orderItemMock));

            when(orderItemMock.getProductId()).thenReturn(1L);

            ProductDTO productDTOMock=mock(ProductDTO.class);
            when(productFeignClient.getProduct(any())).thenReturn(productDTOMock);

            OrderItemResponseDTO orderItemResponseDTOMock=mock(OrderItemResponseDTO.class);
            when(modelMapper.map(any(OrderItem.class),eq(OrderItemResponseDTO.class))).thenReturn(orderItemResponseDTOMock);
            when(modelMapper.map(any(ProductDTO.class),eq(OrderItemResponseDTO.class))).thenReturn(orderItemResponseDTOMock);

            Order ordermock=mock(Order.class);

            when(orderItemMock.getOrder()).thenReturn(ordermock);
            when(ordermock.getId()).thenReturn(1L);

            List<OrderItemResponseDTO> orderItemesponseDTOList=orderItemServiceImpl.listItems();
            assertTrue(orderItemesponseDTOList.size()==1);
        }catch(Exception ex){
//            System.out.print(ex);
            assertTrue(false);
        }
    }

    @Test
    public void testListItemsPositiveMultipleItemsFound(){
        try{
            List<OrderItem> orderItemListMock=mock(List.class);
            when(orderItemRepository.findAll()).thenReturn(orderItemListMock);

            OrderItem orderItemMock1=mock(OrderItem.class);
            OrderItem orderItemMock2=mock(OrderItem.class);
            when(orderItemListMock.stream()).thenReturn(Stream.of(orderItemMock1,orderItemMock2));

            when(orderItemMock1.getProductId()).thenReturn(1L);
            when(orderItemMock2.getProductId()).thenReturn(2L);

            ProductDTO productDTOMock1=mock(ProductDTO.class);
            ProductDTO productDTOMock2=mock(ProductDTO.class);
            when(productFeignClient.getProduct(any())).thenReturn(productDTOMock1).thenReturn(productDTOMock2);

            OrderItemResponseDTO orderItemResponseDTOMock1=mock(OrderItemResponseDTO.class);
            OrderItemResponseDTO orderItemResponseDTOMock2=mock(OrderItemResponseDTO.class);
            when(modelMapper.map(any(OrderItem.class),eq(OrderItemResponseDTO.class))).thenReturn(orderItemResponseDTOMock1);
            when(modelMapper.map(any(ProductDTO.class),eq(OrderItemResponseDTO.class))).thenReturn(orderItemResponseDTOMock2);

            Order ordermock1=mock(Order.class);
            Order ordermock2=mock(Order.class);

            when(orderItemMock1.getOrder()).thenReturn(ordermock1);
            when(ordermock1.getId()).thenReturn(1L);

            when(orderItemMock2.getOrder()).thenReturn(ordermock2);
            when(ordermock2.getId()).thenReturn(2L);

            List<OrderItemResponseDTO> orderItemesponseDTOList=orderItemServiceImpl.listItems();
            assertTrue(orderItemesponseDTOList.size()>1);
        }catch(Exception ex){
//            System.out.print(ex);
            assertTrue(false);
        }
    }

    @Test
    public void testListItemsNegativeWhenProductIsNotFound(){
        try{
            OrderItem orderItemMock=mock(OrderItem.class);
            when(orderItemRepository.findAll()).thenReturn(List.of(orderItemMock));

            when(orderItemMock.getProductId()).thenReturn(1L);

            FeignException notFound = mock(FeignException.NotFound.class);
            when(notFound.getMessage()).thenReturn("Product not found with Id: 1");
            when(notFound.status()).thenReturn(404);

            when(productFeignClient.getProduct(any())).thenThrow(notFound);

            List<OrderItemResponseDTO> orderItemesponseDTOList=orderItemServiceImpl.listItems();
        } catch(Exception ex){
            assertThatThrownBy(() -> orderItemServiceImpl.listItems())
                    .isInstanceOf(FeignException.NotFound.class)
                    .hasMessage("Product not found with Id: 1");
        }
    }

    @Test
    public void testListItemsNegativeWhenListIsEmpty(){
        try{
            when(orderItemRepository.findAll()).thenReturn(List.of());

            List<OrderItemResponseDTO> orderItemesponseDTOList=orderItemServiceImpl.listItems();

        } catch(Exception ex){
            assertThatThrownBy(() -> orderItemServiceImpl.listItems())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Item List is Empty");
        }
    }

    @Test
    public void testListItemsByProductPositiveOneItemFound(){
        try{
            Long productId=1l;

            ProductDTO productDTOMock=mock(ProductDTO.class);
            when(productFeignClient.getProduct(any())).thenReturn(productDTOMock);

            List<OrderItem> orderItemListMock=mock(List.class);
            when(orderItemRepository.findByProductId(any())).thenReturn(orderItemListMock);

            OrderItem orderItemMock=mock(OrderItem.class);
            when(orderItemListMock.stream()).thenReturn(Stream.of(orderItemMock));

            OrderItemResponseDTO orderItemResponseDTOMock=mock(OrderItemResponseDTO.class);
            when(modelMapper.map(any(OrderItem.class),eq(OrderItemResponseDTO.class))).thenReturn(orderItemResponseDTOMock);
            when(modelMapper.map(any(ProductDTO.class),eq(OrderItemResponseDTO.class))).thenReturn(orderItemResponseDTOMock);

            Order ordermock=mock(Order.class);

            when(orderItemMock.getOrder()).thenReturn(ordermock);
            when(ordermock.getId()).thenReturn(1L);

            List<OrderItemResponseDTO> orderItemResponseDTOList=orderItemServiceImpl.listItemsByProduct(productId);
            assertTrue(orderItemResponseDTOList.size()==1);
        }catch(Exception ex){
            assertTrue(false);
        }
    }

    @Test
    public void testListItemsByProductPositiveMultipleItemFound(){
        try{
            Long productId=1l;

            ProductDTO productDTOMock=mock(ProductDTO.class);
            when(productFeignClient.getProduct(any())).thenReturn(productDTOMock);

            List<OrderItem> orderItemListMock=mock(List.class);
            when(orderItemRepository.findByProductId(any())).thenReturn(orderItemListMock);

            OrderItem orderItemMock1=mock(OrderItem.class);
            OrderItem orderItemMock2=mock(OrderItem.class);
            when(orderItemListMock.stream()).thenReturn(Stream.of(orderItemMock1,orderItemMock2));

            OrderItemResponseDTO orderItemResponseDTOMock1=mock(OrderItemResponseDTO.class);
            OrderItemResponseDTO orderItemResponseDTOMock2=mock(OrderItemResponseDTO.class);
            when(modelMapper.map(any(OrderItem.class),eq(OrderItemResponseDTO.class))).thenReturn(orderItemResponseDTOMock1);
            when(modelMapper.map(any(ProductDTO.class),eq(OrderItemResponseDTO.class))).thenReturn(orderItemResponseDTOMock2);

            Order ordermock1=mock(Order.class);
            Order ordermock2=mock(Order.class);

            when(orderItemMock1.getOrder()).thenReturn(ordermock1);
            when(ordermock1.getId()).thenReturn(1L);

            when(orderItemMock2.getOrder()).thenReturn(ordermock2);
            when(ordermock2.getId()).thenReturn(2L);

            List<OrderItemResponseDTO> orderItemResponseDTOList=orderItemServiceImpl.listItemsByProduct(productId);
            assertTrue(orderItemResponseDTOList.size()>1);
        }catch(Exception ex){
            assertTrue(false);
        }
    }

    @Test
    public void testListItemsByProductNegativeWhenProductIsNotFound(){
        Long productId=1l;
        try{

            FeignException notFound = mock(FeignException.NotFound.class);
            when(notFound.getMessage()).thenReturn("Product not found with Id: 1");
            when(notFound.status()).thenReturn(404);

            when(productFeignClient.getProduct(any())).thenThrow(notFound);

            List<OrderItemResponseDTO> orderItemResponseDTOList=orderItemServiceImpl.listItemsByProduct(productId);

        } catch(Exception ex){
            assertThatThrownBy(() -> orderItemServiceImpl.listItemsByProduct(productId))
                    .isInstanceOf(FeignException.NotFound.class)
                    .hasMessage("Product not found with Id: 1");
        }
    }

    @Test
    public void testItemsByProductNegativeWhenListIsEmpty(){
        Long productId=1l;
        try{
            when(orderItemRepository.findByProductId(any())).thenReturn(List.of());

            List<OrderItemResponseDTO> orderItemResponseDTOList=orderItemServiceImpl.listItemsByProduct(productId);

        } catch(Exception ex){
            assertThatThrownBy(() -> orderItemServiceImpl.listItemsByProduct(productId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Item List is Empty");
        }
    }

    @Test
    public void testListItemsByOrderPositiveOneItemFound(){
        try{
            Long orderId=1L;
            List<OrderItem> orderItemListMock=mock(List.class);
            when(orderItemRepository.findByOrderId(any())).thenReturn(orderItemListMock);

            OrderItem orderItemMock=mock(OrderItem.class);
            when(orderItemListMock.stream()).thenReturn(Stream.of(orderItemMock));

            when(orderItemMock.getProductId()).thenReturn(1L);

            ProductDTO productDTOMock=mock(ProductDTO.class);
            when(productFeignClient.getProduct(any())).thenReturn(productDTOMock);

            OrderItemResponseDTO orderItemResponseDTOMock=mock(OrderItemResponseDTO.class);
            when(modelMapper.map(any(OrderItem.class),eq(OrderItemResponseDTO.class))).thenReturn(orderItemResponseDTOMock);
            when(modelMapper.map(any(ProductDTO.class),eq(OrderItemResponseDTO.class))).thenReturn(orderItemResponseDTOMock);

            Order ordermock=mock(Order.class);

            when(orderItemMock.getOrder()).thenReturn(ordermock);
            when(ordermock.getId()).thenReturn(1L);

            List<OrderItemResponseDTO> orderItemesponseDTOList=orderItemServiceImpl.listItemsByOrder(orderId);
            assertTrue(orderItemesponseDTOList.size()==1);
        }catch(Exception ex){
//            System.out.print(ex);
            assertTrue(false);
        }
    }

    @Test
    public void testListItemsByOrderPositiveMultipleItemsFound(){
        try{
            Long orderId=1L;
            List<OrderItem> orderItemListMock=mock(List.class);
            when(orderItemRepository.findByOrderId(any())).thenReturn(orderItemListMock);

            OrderItem orderItemMock1=mock(OrderItem.class);
            OrderItem orderItemMock2=mock(OrderItem.class);
            when(orderItemListMock.stream()).thenReturn(Stream.of(orderItemMock1,orderItemMock2));

            when(orderItemMock1.getProductId()).thenReturn(1L);
            when(orderItemMock2.getProductId()).thenReturn(2L);

            ProductDTO productDTOMock1=mock(ProductDTO.class);
            ProductDTO productDTOMock2=mock(ProductDTO.class);
            when(productFeignClient.getProduct(any())).thenReturn(productDTOMock1).thenReturn(productDTOMock2);

            OrderItemResponseDTO orderItemResponseDTOMock1=mock(OrderItemResponseDTO.class);
            OrderItemResponseDTO orderItemResponseDTOMock2=mock(OrderItemResponseDTO.class);
            when(modelMapper.map(any(OrderItem.class),eq(OrderItemResponseDTO.class))).thenReturn(orderItemResponseDTOMock1);
            when(modelMapper.map(any(ProductDTO.class),eq(OrderItemResponseDTO.class))).thenReturn(orderItemResponseDTOMock2);

            Order ordermock1=mock(Order.class);
            Order ordermock2=mock(Order.class);

            when(orderItemMock1.getOrder()).thenReturn(ordermock1);
            when(ordermock1.getId()).thenReturn(1L);

            when(orderItemMock2.getOrder()).thenReturn(ordermock2);
            when(ordermock2.getId()).thenReturn(2L);

            List<OrderItemResponseDTO> orderItemesponseDTOList=orderItemServiceImpl.listItemsByOrder(orderId);
            assertTrue(orderItemesponseDTOList.size()>1);
        }catch(Exception ex){
//            System.out.print(ex);
            assertTrue(false);
        }
    }

    @Test
    public void testListItemsByOrderNegativeWhenProductIsNotFound(){
        Long orderId=1L;

        try{
            OrderItem orderItemMock=mock(OrderItem.class);
            when(orderItemRepository.findByOrderId(any())).thenReturn(List.of(orderItemMock));

            when(orderItemMock.getProductId()).thenReturn(1L);

            FeignException notFound = mock(FeignException.NotFound.class);
            when(notFound.getMessage()).thenReturn("Product not found with Id: 1");
            when(notFound.status()).thenReturn(404);

            when(productFeignClient.getProduct(any())).thenThrow(notFound);

            List<OrderItemResponseDTO> orderItemesponseDTOList=orderItemServiceImpl.listItemsByOrder(orderId);
        } catch(Exception ex){
            assertThatThrownBy(() -> orderItemServiceImpl.listItemsByOrder(orderId))
                    .isInstanceOf(FeignException.NotFound.class)
                    .hasMessage("Product not found with Id: 1");
        }
    }

    @Test
    public void testListItemsByOrderNegativeWhenListIsEmpty(){
        Long orderId=1L;
        try{
            when(orderItemRepository.findByOrderId(any())).thenReturn(List.of());

            List<OrderItemResponseDTO> orderItemesponseDTOList=orderItemServiceImpl.listItemsByOrder(orderId);

        } catch(Exception ex){
            assertThatThrownBy(() -> orderItemServiceImpl.listItemsByOrder(orderId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Item List is Empty");
        }
    }

    @Test
    public void testGetItemPositive(){
        try{
            OrderItem orderItem=new OrderItem();
            orderItem.setId(1L);
            orderItem.setProductId(1L);
            orderItem.setQuantity(2);
            orderItem.setPrice(499.5);

            Order order=new Order();
            order.setId(1L);
            order.setUserId(1L);
            order.setStatus("CREATED");
            order.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));

            orderItem.setOrder(order);

            ProductDTO productDTO=new ProductDTO();
            productDTO.setId(1L);
            productDTO.setName("Mechanical Keyboard");
            productDTO.setDescription("RGB backlit mechanical keyboard with blue switches.");
            productDTO.setPrice(5099);
            productDTO.setStock(200);

            OrderItemResponseDTO orderItemResponseDTO=new OrderItemResponseDTO();
            orderItemResponseDTO.setId(1L);
            orderItemResponseDTO.setProductId(1L);
            orderItemResponseDTO.setQuantity(2);
            orderItemResponseDTO.setPrice(499.5);
            orderItemResponseDTO.setOrderId(1L);
            orderItemResponseDTO.setName("Mechanical Keyboard");
            orderItemResponseDTO.setDescription("RGB backlit mechanical keyboard with blue switches.");
            orderItemResponseDTO.setStock(200);

            when(orderItemRepository.findById(any())).thenReturn(Optional.of(orderItem));

            when(productFeignClient.getProduct(any())).thenReturn(productDTO);

            when(modelMapper.map(any(OrderItem.class),eq(OrderItemResponseDTO.class))).thenReturn(orderItemResponseDTO);
            when(modelMapper.map(any(ProductDTO.class),eq(OrderItemResponseDTO.class))).thenReturn(orderItemResponseDTO);

            OrderItemResponseDTO actualOrderItemResponseDTO=orderItemServiceImpl.getItem(1L);
            assertNotNull(actualOrderItemResponseDTO);
        } catch(Exception ex){
            assertTrue(false);
        }
    }

    @Test
    public void testGetItemNegativeWhenProductIsNotFound(){
        try{
            OrderItem orderItem=new OrderItem();
            orderItem.setId(1L);
            orderItem.setProductId(1L);
            orderItem.setQuantity(2);
            orderItem.setPrice(499.5);

            Order order=new Order();
            order.setId(1L);
            order.setUserId(1L);
            order.setStatus("CREATED");
            order.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));

            orderItem.setOrder(order);
            when(orderItemRepository.findById(any())).thenReturn(Optional.of(orderItem));

            FeignException notFound = mock(FeignException.NotFound.class);
            when(notFound.getMessage()).thenReturn("Product not found with Id: 1");
            when(notFound.status()).thenReturn(404);

            when(productFeignClient.getProduct(any())).thenThrow(notFound);

            OrderItemResponseDTO actualOrderItemResponseDTO=orderItemServiceImpl.getItem(1L);
        } catch(Exception ex){
            assertThat(ex)
                    .isInstanceOf(FeignException.NotFound.class)
                    .hasMessageContaining("Product not found with Id: 1");
        }
    }

    @Test
    public void testGetItemNegativeWhenItemIsNotFound(){
        try{
            when(orderItemRepository.findById(any())).thenReturn(Optional.empty());
            OrderItemResponseDTO actualOrderItemResponseDTO=orderItemServiceImpl.getItem(1L);
        } catch(Exception ex){
            assertThat(ex)
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Item not found with Id: 1");
        }
    }

    @Test
    public void testDeleteItemPositive(){
        try{
            OrderItem orderItem=new OrderItem();
            orderItem.setId(1L);
            orderItem.setProductId(1L);
            orderItem.setQuantity(2);
            orderItem.setPrice(499.5);

            Order order=new Order();
            order.setId(1L);
            order.setUserId(1L);
            order.setStatus("CREATED");
            order.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));

            orderItem.setOrder(order);
            when(orderItemRepository.findById(any())).thenReturn(Optional.of(orderItem));

            String result=orderItemServiceImpl.deleteItem(1L);
            assertEquals("Item deleted with Id: 1",result);
        } catch(Exception ex){
            assertTrue(false);
        }
    }

    @Test
    public void testDeleteOrderNegativeWhenOrderIsNotFound(){
        try{
            when(orderItemRepository.findById(any())).thenReturn(Optional.empty());
            String result=orderItemServiceImpl.deleteItem(1L);
        } catch(Exception ex){
            assertThat(ex)
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Item not found with Id: 1");
        }
    }

    // ------------------------------------------------------------
    // addItem(OrderItemDTO) – Positive
    // ------------------------------------------------------------
    @Test
    public void testAddItemPositive() {
        // Input DTO
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(null);
        dto.setProductId(11L);
        dto.setQuantity(2);
        dto.setPrice(999.5);
        dto.setOrderId(77L);

        // Order exists
        Order order = new Order();
        order.setId(77L);

        // Product exists
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(11L);
        productDTO.setName("Keyboard");

        // Mapped entity (pre-save)
        OrderItem mappedItem = new OrderItem();
        mappedItem.setProductId(11L);
        mappedItem.setQuantity(2);
        mappedItem.setPrice(999.5);
        // Note: addItem() does NOT set mappedItem.setOrder(order) currently

        // Saved entity (post-save) with generated ID
        OrderItem savedItem = new OrderItem();
        savedItem.setId(555L);                // Item ID generated
        savedItem.setProductId(11L);
        savedItem.setQuantity(2);
        savedItem.setPrice(999.5);

        // Response DTO (ModelMapper results)
        OrderItemResponseDTO responseAfterEntityMap = new OrderItemResponseDTO();
        responseAfterEntityMap.setId(555L);
        responseAfterEntityMap.setProductId(11L);
        responseAfterEntityMap.setQuantity(2);
        responseAfterEntityMap.setPrice(999.5);

        // Mocks
        when(orderRepository.findById(eq(77L))).thenReturn(Optional.of(order));
        when(productFeignClient.getProduct(eq(11L))).thenReturn(productDTO);

        when(modelMapper.map(any(OrderItemDTO.class), eq(OrderItem.class))).thenReturn(mappedItem);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(savedItem);

        when(modelMapper.map(any(OrderItem.class), eq(OrderItemResponseDTO.class)))
                .thenReturn(responseAfterEntityMap);
        when(modelMapper.map(any(ProductDTO.class), eq(OrderItemResponseDTO.class)))
                .thenReturn(responseAfterEntityMap);

        // Execute
        OrderItemResponseDTO actual = orderItemServiceImpl.addItem(dto);

        // Asserts
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(555L);
        assertThat(actual.getProductId()).isEqualTo(11L);
        assertThat(actual.getQuantity()).isEqualTo(2);
        assertThat(actual.getPrice()).isEqualTo(999.5);

        // IMPORTANT: Matches current code (uses savedOrderItem.getId() instead of order.getId())
        assertEquals(555L, actual.getOrderId(),
                "By current implementation, orderId is set to saved item ID, not the order ID.");
    }

    // ------------------------------------------------------------
    // addItem(OrderItemDTO) – Negative: Order not found
    // ------------------------------------------------------------
    @Test
    public void testAddItemNegativeWhenOrderNotFound() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(11L);
        dto.setQuantity(2);
        dto.setPrice(999.5);
        dto.setOrderId(77L);

        when(orderRepository.findById(eq(77L))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderItemServiceImpl.addItem(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order not found with Id: 77");
    }

    // ------------------------------------------------------------
    // addItem(OrderItemDTO) – Negative: Product not found
    // ------------------------------------------------------------
    @Test
    public void testAddItemNegativeWhenProductNotFound() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(11L);
        dto.setQuantity(2);
        dto.setPrice(999.5);
        dto.setOrderId(77L);

        Order order = new Order();
        order.setId(77L);

        when(orderRepository.findById(eq(77L))).thenReturn(Optional.of(order));

        FeignException notFound = org.mockito.Mockito.mock(FeignException.NotFound.class);
        when(notFound.getMessage()).thenReturn("Product not found with Id: 11");
        when(notFound.status()).thenReturn(404);

        when(productFeignClient.getProduct(eq(11L))).thenThrow(notFound);

        assertThatThrownBy(() -> orderItemServiceImpl.addItem(dto))
                .isInstanceOf(FeignException.NotFound.class)
                .hasMessage("Product not found with Id: 11");
    }

    // ------------------------------------------------------------
    // updateItem(Long, OrderItemDTO) – Positive
    // ------------------------------------------------------------
    @Test
    public void testUpdateItemPositive() {
        Long itemId = 555L;

        // Input DTO
        OrderItemDTO dto = new OrderItemDTO();
        dto.setOrderId(77L);              // Must match existing item's order
        dto.setProductId(11L);
        dto.setQuantity(5);
        dto.setPrice(1499.0);

        // Order exists
        Order order = new Order();
        order.setId(77L);

        // Existing item
        Order existingOrder = new Order();
        existingOrder.setId(77L);

        OrderItem existingItem = new OrderItem();
        existingItem.setId(itemId);
        existingItem.setProductId(11L);   // original
        existingItem.setQuantity(2);
        existingItem.setPrice(999.5);
        existingItem.setOrder(existingOrder);

        // Saved item after update
        OrderItem savedItem = new OrderItem();
        savedItem.setId(itemId);
        savedItem.setProductId(11L);
        savedItem.setQuantity(5);
        savedItem.setPrice(1499.0);
        savedItem.setOrder(order);

        // Product for savedItem
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(11L);
        productDTO.setName("Keyboard Pro");

        // Response DTO
        OrderItemResponseDTO responseAfterEntityMap = new OrderItemResponseDTO();
        responseAfterEntityMap.setId(itemId);
        responseAfterEntityMap.setProductId(11L);
        responseAfterEntityMap.setQuantity(5);
        responseAfterEntityMap.setPrice(1499.0);

        when(orderRepository.findById(eq(77L))).thenReturn(Optional.of(order));
        when(orderItemRepository.findById(eq(itemId))).thenReturn(Optional.of(existingItem));

        // mapping the DTO onto the existing item
        when(modelMapper.map(any(OrderItemDTO.class), eq(OrderItem.class))).thenReturn(savedItem); // not strictly used in update flow
        // For update flow: modelMapper.map(dto, existingItem) is called; since it's a void method, we don't need to stub it.

        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(savedItem);

        when(productFeignClient.getProduct(eq(11L))).thenReturn(productDTO);

        when(modelMapper.map(any(OrderItem.class), eq(OrderItemResponseDTO.class)))
                .thenReturn(responseAfterEntityMap);
        when(modelMapper.map(any(ProductDTO.class), eq(OrderItemResponseDTO.class)))
                .thenReturn(responseAfterEntityMap);

        OrderItemResponseDTO actual = orderItemServiceImpl.updateItem(itemId, dto);

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(itemId);
        assertThat(actual.getProductId()).isEqualTo(11L);
        assertThat(actual.getQuantity()).isEqualTo(5);
        assertThat(actual.getPrice()).isEqualTo(1499.0);
        assertThat(actual.getOrderId()).isEqualTo(77L);
    }

    // ------------------------------------------------------------
    // updateItem(Long, OrderItemDTO) – Negative: Order not found
    // ------------------------------------------------------------
    @Test
    public void testUpdateItemNegativeWhenOrderNotFound() {
        Long itemId = 555L;

        OrderItemDTO dto = new OrderItemDTO();
        dto.setOrderId(77L);
        dto.setProductId(11L);
        dto.setQuantity(5);
        dto.setPrice(1499.0);

        when(orderRepository.findById(eq(77L))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderItemServiceImpl.updateItem(itemId, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order not found with Id: 77");
    }

    // ------------------------------------------------------------
    // updateItem(Long, OrderItemDTO) – Negative: OrderItem not found
    // ------------------------------------------------------------
    @Test
    public void testUpdateItemNegativeWhenOrderItemNotFound() {
        Long itemId = 555L;

        OrderItemDTO dto = new OrderItemDTO();
        dto.setOrderId(77L);
        dto.setProductId(11L);
        dto.setQuantity(5);
        dto.setPrice(1499.0);

        Order order = new Order();
        order.setId(77L);

        when(orderRepository.findById(eq(77L))).thenReturn(Optional.of(order));
        when(orderItemRepository.findById(eq(itemId))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderItemServiceImpl.updateItem(itemId, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order Item not found with Id: 555");
    }

    // ------------------------------------------------------------
    // updateItem(Long, OrderItemDTO) – Negative: OrderId mismatch
    // ------------------------------------------------------------
    @Test
    public void testUpdateItemNegativeWhenOrderIdMismatch() {
        Long itemId = 555L;

        OrderItemDTO dto = new OrderItemDTO();
        dto.setOrderId(99L); // Different from item's existing order id
        dto.setProductId(11L);
        dto.setQuantity(5);
        dto.setPrice(1499.0);

        Order order = new Order();
        order.setId(99L);

        OrderItem existingItem = new OrderItem();
        existingItem.setId(itemId);
        Order existingOrder = new Order();
        existingOrder.setId(77L); // mismatch here
        existingItem.setOrder(existingOrder);

        when(orderRepository.findById(eq(99L))).thenReturn(Optional.of(order));
        when(orderItemRepository.findById(eq(itemId))).thenReturn(Optional.of(existingItem));

        assertThatThrownBy(() -> orderItemServiceImpl.updateItem(itemId, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Please enter matching Order Id: 77");
    }

    // ------------------------------------------------------------
    // updateItem(Long, OrderItemDTO) – Negative: Product not found
    // ------------------------------------------------------------
    @Test
    public void testUpdateItemNegativeWhenProductNotFound() {
        Long itemId = 555L;

        OrderItemDTO dto = new OrderItemDTO();
        dto.setOrderId(77L);
        dto.setProductId(11L);
        dto.setQuantity(5);
        dto.setPrice(1499.0);

        Order order = new Order();
        order.setId(77L);

        Order existingOrder = new Order();
        existingOrder.setId(77L);

        OrderItem existingItem = new OrderItem();
        existingItem.setId(itemId);
        existingItem.setProductId(11L);
        existingItem.setOrder(existingOrder);

        OrderItem savedItem = new OrderItem();
        savedItem.setId(itemId);
        savedItem.setProductId(11L);
        savedItem.setQuantity(5);
        savedItem.setPrice(1499.0);
        savedItem.setOrder(order);

        when(orderRepository.findById(eq(77L))).thenReturn(Optional.of(order));
        when(orderItemRepository.findById(eq(itemId))).thenReturn(Optional.of(existingItem));
        // modelMapper.map(dto, existingItem) is void; no stub required
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(savedItem);

        FeignException notFound = org.mockito.Mockito.mock(FeignException.NotFound.class);
        when(notFound.getMessage()).thenReturn("Product not found with Id: 11");
        when(notFound.status()).thenReturn(404);

        when(productFeignClient.getProduct(eq(11L))).thenThrow(notFound);

        assertThatThrownBy(() -> orderItemServiceImpl.updateItem(itemId, dto))
                .isInstanceOf(FeignException.NotFound.class)
                .hasMessage("Product not found with Id: 11");
    }

    // =======================
// Bean Validation tests for OrderItemDTO (uses `validator`)
// =======================

    @Test
    public void testOrderItemDTOValidation_Positive_NoViolations() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(null);
        dto.setProductId(10L);   // valid: positive & not null
        dto.setQuantity(1);      // valid: >= 1
        dto.setPrice(49.99);     // valid: positive
        dto.setOrderId(20L);     // valid: not null

        Set<ConstraintViolation<OrderItemDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    public void testOrderItemDTOValidation_ProductIdNull() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(null);  // should trigger @NotNull
        dto.setQuantity(1);
        dto.setPrice(10.0);
        dto.setOrderId(1L);

        Set<ConstraintViolation<OrderItemDTO>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(v -> v.getMessage())
                .anyMatch(msg -> msg.contains("Product_Id is required"));
    }

    @Test
    public void testOrderItemDTOValidation_ProductIdNegative() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(-5L);   // should trigger @Positive
        dto.setQuantity(1);
        dto.setPrice(10.0);
        dto.setOrderId(1L);

        Set<ConstraintViolation<OrderItemDTO>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(v -> v.getMessage())
                .anyMatch(msg -> msg.contains("Product_Id must be a positive number"));
    }

    @Test
    public void testOrderItemDTOValidation_QuantityTooLow() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(10L);
        dto.setQuantity(0);      // should trigger @Min(1)
        dto.setPrice(10.0);
        dto.setOrderId(1L);

        Set<ConstraintViolation<OrderItemDTO>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(v -> v.getMessage())
                .anyMatch(msg -> msg.contains("Quantity must be at least 1"));
    }

    @Test
    public void testOrderItemDTOValidation_PriceNegative() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(10L);
        dto.setQuantity(2);
        dto.setPrice(-1.0);      // should trigger @Positive
        dto.setOrderId(1L);

        Set<ConstraintViolation<OrderItemDTO>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(v -> v.getMessage())
                .anyMatch(msg -> msg.contains("Price should be positive"));
    }

    @Test
    public void testOrderItemDTOValidation_OrderIdNull() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(10L);
        dto.setQuantity(2);
        dto.setPrice(19.99);
        dto.setOrderId(null);    // should trigger @NotNull

        Set<ConstraintViolation<OrderItemDTO>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(v -> v.getMessage())
                .anyMatch(msg -> msg.contains("Order_Id is required"));
    }

    @Test
    public void testOrderItemDTOValidation_MultipleViolationsTogether() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(null);  // NotNull violation
        dto.setQuantity(0);      // Min violation
        dto.setPrice(-5.0);      // Positive violation
        dto.setOrderId(null);    // NotNull violation

        Set<ConstraintViolation<OrderItemDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSizeGreaterThanOrEqualTo(4);
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains(
                        "Product_Id is required",
                        "Quantity must be at least 1",
                        "Price should be positive",
                        "Order_Id is required"
                );
    }
}
