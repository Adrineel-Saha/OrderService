package com.cognizant.orderservice.services;

import com.cognizant.orderservice.dtos.*;
import com.cognizant.orderservice.entities.Order;
import com.cognizant.orderservice.entities.OrderItem;
import com.cognizant.orderservice.exceptions.ResourceNotFoundException;
import com.cognizant.orderservice.feignclients.ProductFeignClient;
import com.cognizant.orderservice.repositories.OrderItemRepository;
import com.cognizant.orderservice.repositories.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class OrderItemServiceImpl implements OrderItemService{
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private Map<Long, ProductDTO> productCache;

    @KafkaListener(topics = "${app.kafka.productproducer.topic}", groupId = "orderServiceGroup")
    public void consumeProductEvent(ProductDTO productDTO) {
        productCache.put(productDTO.getId(), productDTO);
        log.info("Received and cached ProductDTO from product-events: {}", productDTO);
    }

    @CircuitBreaker(name = "OrderMicroservice", fallbackMethod = "addItemGetDefaultProduct")
    @Override
    public OrderItemResponseDTO addItem(OrderItemDTO orderItemDTO) {
        Long orderId=orderItemDTO.getOrderId();
        Order order=orderRepository.findById(orderId).orElseThrow(
                ()->new RuntimeException("Order not found with Id: "+ orderId)
        );

        Long productId=orderItemDTO.getProductId();
        ProductDTO productDTO=productFeignClient.getProduct(productId);

        OrderItem orderItem=modelMapper.map(orderItemDTO,OrderItem.class);
        OrderItem savedOrderItem=orderItemRepository.save(orderItem);

        OrderItemResponseDTO orderItemResponseDTO=modelMapper.map(savedOrderItem,OrderItemResponseDTO.class);
        modelMapper.map(productDTO,orderItemResponseDTO);
        orderItemResponseDTO.setOrderId(savedOrderItem.getId());
        return orderItemResponseDTO;
    }

    public OrderItemResponseDTO addItemGetDefaultProduct(OrderItemDTO orderItemDTO , Throwable throwable) {
        Long orderId=orderItemDTO.getOrderId();
        orderRepository.findById(orderId).orElseThrow(
                ()->new RuntimeException("Order not found with Id: "+ orderId)
        );

        Long productId=orderItemDTO.getProductId();
        ProductDTO productDTO=productCache.getOrDefault(productId, getFallbackProduct(productId));

        OrderItem orderItem=modelMapper.map(orderItemDTO,OrderItem.class);
        OrderItem savedOrderItem=orderItemRepository.save(orderItem);

        OrderItemResponseDTO orderItemResponseDTO=modelMapper.map(savedOrderItem,OrderItemResponseDTO.class);
        modelMapper.map(productDTO,orderItemResponseDTO);
        orderItemResponseDTO.setOrderId(savedOrderItem.getId());
        return orderItemResponseDTO;
    }

    @CircuitBreaker(name = "OrderMicroservice", fallbackMethod = "getItemGetDefaultProduct")
    @Override
    public OrderItemResponseDTO getItem(Long itemId) {
        OrderItem orderItem=orderItemRepository.findById(itemId).orElseThrow(
                ()->new ResourceNotFoundException("Item not found with Id: "+ itemId)
        );
        Long productId=orderItem.getProductId();
        ProductDTO productDTO=productFeignClient.getProduct(productId);

        OrderItemResponseDTO orderItemResponseDTO=modelMapper.map(orderItem, OrderItemResponseDTO.class);
        modelMapper.map(productDTO,orderItemResponseDTO);
        orderItemResponseDTO.setOrderId(orderItem.getOrder().getId());
        return orderItemResponseDTO;
    }

    public OrderItemResponseDTO getItemGetDefaultProduct(Long itemId , Throwable throwable) {
        OrderItem orderItem=orderItemRepository.findById(itemId).orElseThrow(
                ()->new ResourceNotFoundException("Item not found with Id: "+ itemId)
        );
        Long productId=orderItem.getProductId();
        ProductDTO productDTO=productCache.getOrDefault(productId, getFallbackProduct(productId));

        OrderItemResponseDTO orderItemResponseDTO=modelMapper.map(orderItem, OrderItemResponseDTO.class);
        modelMapper.map(productDTO,orderItemResponseDTO);
        orderItemResponseDTO.setOrderId(orderItem.getOrder().getId());
        return orderItemResponseDTO;
    }

    @CircuitBreaker(name = "OrderMicroservice", fallbackMethod = "listItemsGetDefaultProduct")
    @Override
    public List<OrderItemResponseDTO> listItems() {
        List<OrderItem> orderItemList=orderItemRepository.findAll();
        List<OrderItemResponseDTO> orderItemResponseDTOList=orderItemList.stream().
                map(orderItem->{
                    Long productId= orderItem.getProductId();
                    ProductDTO productDTO=productFeignClient.getProduct(productId);
                    OrderItemResponseDTO orderItemResponseDTO=modelMapper.map(orderItem, OrderItemResponseDTO.class);
                    modelMapper.map(productDTO,orderItemResponseDTO);
                    orderItemResponseDTO.setOrderId(orderItem.getOrder().getId());
                    return orderItemResponseDTO;
                }).toList();

        if(orderItemResponseDTOList.isEmpty()){
            throw new RuntimeException("Item List is Empty");
        }

        return orderItemResponseDTOList;
    }

    public List<OrderItemResponseDTO> listItemsGetDefaultProduct(Throwable throwable) {
        List<OrderItem> orderItemList=orderItemRepository.findAll();
        List<OrderItemResponseDTO> orderItemResponseDTOList=orderItemList.stream().
                map(orderItem->{
                    Long productId= orderItem.getProductId();
                    ProductDTO productDTO=productCache.getOrDefault(productId, getFallbackProduct(productId));
                    OrderItemResponseDTO orderItemResponseDTO=modelMapper.map(orderItem, OrderItemResponseDTO.class);
                    modelMapper.map(productDTO,orderItemResponseDTO);
                    orderItemResponseDTO.setOrderId(orderItem.getOrder().getId());
                    return orderItemResponseDTO;
                }).toList();

        if(orderItemResponseDTOList.isEmpty()){
            throw new RuntimeException("Item List is Empty");
        }

        return orderItemResponseDTOList;
    }

    @CircuitBreaker(name = "OrderMicroservice", fallbackMethod = "listItemsByProductGetDefaultProduct")
    @Override
    public List<OrderItemResponseDTO> listItemsByProduct(Long productId) {
        ProductDTO productDTO=productFeignClient.getProduct(productId);

        List<OrderItem> orderItemList=orderItemRepository.findByProductId(productId);

        List<OrderItemResponseDTO> orderItemResponseDTOList=orderItemList.stream().map(
                orderItem->{
                    OrderItemResponseDTO orderItemResponseDTO=modelMapper.map(orderItem,OrderItemResponseDTO.class);
                    modelMapper.map(productDTO,orderItemResponseDTO);
                    orderItemResponseDTO.setOrderId(orderItem.getOrder().getId());
                    return orderItemResponseDTO;
                }
        ).toList();

        if(orderItemResponseDTOList.isEmpty()){
            throw new RuntimeException("Item List is Empty");
        }

        return orderItemResponseDTOList;
    }

    public List<OrderItemResponseDTO> listItemsByProductGetDefaultProduct(Long productId , Throwable throwable) {
        ProductDTO productDTO=productCache.getOrDefault(productId, getFallbackProduct(productId));

        List<OrderItem> orderItemList=orderItemRepository.findByProductId(productId);

        List<OrderItemResponseDTO> orderItemResponseDTOList=orderItemList.stream().map(
                orderItem->{
                    OrderItemResponseDTO orderItemResponseDTO=modelMapper.map(orderItem,OrderItemResponseDTO.class);
                    modelMapper.map(productDTO,orderItemResponseDTO);
                    orderItemResponseDTO.setOrderId(orderItem.getOrder().getId());
                    return orderItemResponseDTO;
                }
        ).toList();

        if(orderItemResponseDTOList.isEmpty()){
            throw new RuntimeException("Item List is Empty");
        }

        return orderItemResponseDTOList;
    }

    @CircuitBreaker(name = "OrderMicroservice", fallbackMethod = "listItemsByOrderGetDefaultProduct")
    @Override
    public List<OrderItemResponseDTO> listItemsByOrder(Long orderId) {
        List<OrderItem> orderItemList=orderItemRepository.findByOrderId(orderId);
        List<OrderItemResponseDTO> orderItemResponseDTOList=orderItemList.stream().map(
                orderItem->{
                    Long productId=orderItem.getProductId();
                    ProductDTO productDTO=productFeignClient.getProduct(productId);
                    OrderItemResponseDTO orderItemResponseDTO=modelMapper.map(orderItem,OrderItemResponseDTO.class);
                    modelMapper.map(productDTO,orderItemResponseDTO);
                    orderItemResponseDTO.setOrderId(orderId);
                    return orderItemResponseDTO;
                }
        ).toList();

        if(orderItemResponseDTOList.isEmpty()){
            throw new RuntimeException("Item List is Empty");
        }

        return orderItemResponseDTOList;
    }

    public List<OrderItemResponseDTO> listItemsByOrderGetDefaultProduct(Long orderId , Throwable throwable) {
        List<OrderItem> orderItemList=orderItemRepository.findByOrderId(orderId);
        List<OrderItemResponseDTO> orderItemResponseDTOList=orderItemList.stream().map(
                orderItem->{
                    Long productId=orderItem.getProductId();
                    ProductDTO productDTO=productCache.getOrDefault(productId, getFallbackProduct(productId));
                    OrderItemResponseDTO orderItemResponseDTO=modelMapper.map(orderItem,OrderItemResponseDTO.class);
                    modelMapper.map(productDTO,orderItemResponseDTO);
                    orderItemResponseDTO.setOrderId(orderId);
                    return orderItemResponseDTO;
                }
        ).toList();

        if(orderItemResponseDTOList.isEmpty()){
            throw new RuntimeException("Item List is Empty");
        }

        return orderItemResponseDTOList;
    }

    @CircuitBreaker(name = "OrderMicroservice", fallbackMethod = "updateItemGetDefaultProduct")
    @Override
    public OrderItemResponseDTO updateItem(Long itemId, OrderItemDTO orderItemDTO) {
        orderItemDTO.setId(itemId);
        Long orderId=orderItemDTO.getOrderId();

        Order order=orderRepository.findById(orderItemDTO.getOrderId()).orElseThrow(
                ()->new RuntimeException("Order not found with Id: "+ orderId)
        );

        OrderItem orderItem=orderItemRepository.findById(itemId).orElseThrow(
                ()->new RuntimeException("Order Item not found with Id: "+ itemId)
        );

        if(orderItem.getOrder().getId()!=orderId){
            throw new RuntimeException("Please enter matching Order Id: "+ orderItem.getOrder().getId());
        }

        modelMapper.map(orderItemDTO,orderItem);
        orderItem.setOrder(order);

        OrderItem savedOrderItem=orderItemRepository.save(orderItem);

        Long productId= savedOrderItem.getProductId();
        ProductDTO productDTO=productFeignClient.getProduct(productId);

        OrderItemResponseDTO orderItemResponseDTO=modelMapper.map(savedOrderItem, OrderItemResponseDTO.class);
        modelMapper.map(productDTO,orderItemResponseDTO);
        orderItemResponseDTO.setOrderId(orderId);
        return orderItemResponseDTO;
    }

    public OrderItemResponseDTO updateItemGetDefaultProduct(Long itemId, OrderItemDTO orderItemDTO , Throwable throwable) {
        orderItemDTO.setId(itemId);
        Long orderId=orderItemDTO.getOrderId();

        Order order=orderRepository.findById(orderItemDTO.getOrderId()).orElseThrow(
                ()->new RuntimeException("Order not found with Id: "+ orderId)
        );

        OrderItem orderItem=orderItemRepository.findById(itemId).orElseThrow(
                ()->new RuntimeException("Order Item not found with Id: "+ itemId)
        );

        if(orderItem.getOrder().getId()!=orderId){
            throw new RuntimeException("Please enter matching Order Id: "+ orderItem.getOrder().getId());
        }

        modelMapper.map(orderItemDTO,orderItem);
        orderItem.setOrder(order);

        OrderItem savedOrderItem=orderItemRepository.save(orderItem);

        Long productId= savedOrderItem.getProductId();
        ProductDTO productDTO=productCache.getOrDefault(productId, getFallbackProduct(productId));

        OrderItemResponseDTO orderItemResponseDTO=modelMapper.map(savedOrderItem, OrderItemResponseDTO.class);
        modelMapper.map(productDTO,orderItemResponseDTO);
        orderItemResponseDTO.setOrderId(orderId);
        return orderItemResponseDTO;
    }

    @Override
    public String deleteItem(Long itemId) {
        OrderItem orderItem=orderItemRepository.findById(itemId).orElseThrow(
                ()->new ResourceNotFoundException("Item not found with Id: "+ itemId)
        );

        log.info("Deleted Item: " + orderItem);

        orderItemRepository.delete(orderItem);
        return "Item deleted with Id: " + itemId;
    }

    private ProductDTO getFallbackProduct(Long productId) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(productId);
        productDTO.setName("Unknown Product");
        productDTO.setDescription("Product details unavailable");
        productDTO.setPrice(0.0);
        productDTO.setStock(0);
        return productDTO;
    }
}
