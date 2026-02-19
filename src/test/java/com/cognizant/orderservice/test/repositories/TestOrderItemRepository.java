package com.cognizant.orderservice.test.repositories;

import com.cognizant.orderservice.entities.Order;
import com.cognizant.orderservice.entities.OrderItem;
import com.cognizant.orderservice.main.OrderServiceApplication;
import com.cognizant.orderservice.repositories.OrderItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ContextConfiguration(classes = OrderServiceApplication.class)
@ActiveProfiles("test")
public class TestOrderItemRepository {
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testFindAllPositive(){
        OrderItem orderItem=new OrderItem();
        orderItem.setProductId(1L);
        orderItem.setQuantity(2);
        orderItem.setPrice(499.5);

        Order order=new Order();
        order.setUserId(1L);
        order.setStatus("CREATED");
        order.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));

        entityManager.persist(order);

        orderItem.setOrder(order);
        entityManager.persist(orderItem);

        List<OrderItem> orderItemList = orderItemRepository.findAll();
        assertTrue(orderItemList.iterator().hasNext());
    }

    @Test
    public void testFindAllNegative(){
        List<OrderItem> orderItemList = orderItemRepository.findAll();
        assertTrue(!orderItemList.iterator().hasNext());
    }

    @Test
    public void testFindByIdPositive(){
        OrderItem orderItem=new OrderItem();
        orderItem.setProductId(2L);
        orderItem.setQuantity(1);
        orderItem.setPrice(1299);

        Order order=new Order();
        order.setUserId(2L);
        order.setStatus("CREATED");
        order.setCreatedAt(LocalDateTime.of(2026,2,2,11,30,0));

        entityManager.persist(order);

        orderItem.setOrder(order);
        entityManager.persist(orderItem);
        Long id= orderItem.getId();

        Optional<OrderItem> orderItemOptional=orderItemRepository.findById(id);
        assertTrue(orderItemOptional.isPresent());
    }

    @Test
    public void testFindByIdNegative(){
        Optional<OrderItem> orderItemOptional=orderItemRepository.findById(2L);
        assertTrue(!orderItemOptional.isPresent());
    }

    @Test
    public void testSavePositive(){
        OrderItem orderItem=new OrderItem();
        orderItem.setProductId(3L);
        orderItem.setQuantity(3);
        orderItem.setPrice(199.99);

        Order order=new Order();
        order.setUserId(3L);
        order.setStatus("PAID");
        order.setCreatedAt(LocalDateTime.of(2026,2,3,9,15,0));

        entityManager.persist(order);

        orderItem.setOrder(order);
        orderItemRepository.save(orderItem);
        Long id= orderItem.getId();

        Optional<OrderItem> orderItemOptional=orderItemRepository.findById(id);
        assertTrue(orderItemOptional.isPresent());
    }

    @Test
    public void testSaveNegative(){
        Optional<OrderItem> orderItemOptional=orderItemRepository.findById(3L);
        assertTrue(!orderItemOptional.isPresent());
    }

    @Test
    public void deletePositive(){
        OrderItem orderItem=new OrderItem();
        orderItem.setProductId(4L);
        orderItem.setQuantity(1);
        orderItem.setPrice(2499);

        Order order=new Order();
        order.setUserId(4L);
        order.setStatus("SHIPPED");
        order.setCreatedAt(LocalDateTime.of(2026,2,4,14,45,0));

        entityManager.persist(order);

        orderItem.setOrder(order);
        entityManager.persist(orderItem);
        Long id=orderItem.getId();

        orderItemRepository.delete(orderItem);
        Optional<OrderItem> orderItemOptional=orderItemRepository.findById(id);
        assertTrue(!orderItemOptional.isPresent());
    }

    @Test
    public void deleteNegative(){
        Optional<OrderItem> orderItemOptional=orderItemRepository.findById(4L);
        assertTrue(!orderItemOptional.isPresent());
    }

    @Test
    public void testFindByOrderIdPositive(){
        OrderItem orderItem=new OrderItem();
        orderItem.setProductId(5L);
        orderItem.setQuantity(2);
        orderItem.setPrice(1299);

        Order order=new Order();
        order.setUserId(5L);
        order.setStatus("CANCELLED");
        order.setCreatedAt(LocalDateTime.of(2026,2,5,16,20,0));

        entityManager.persist(order);

        orderItem.setOrder(order);
        entityManager.persist(orderItem);
        Long orderId= orderItem.getOrder().getId();

        List<OrderItem> orderItemList = orderItemRepository.findByOrderId(orderId);
        assertTrue(orderItemList.iterator().hasNext());
    }

    @Test
    public void testFindByOrderIdNegative(){
        List<OrderItem> orderItemList = orderItemRepository.findByOrderId(5L);
        assertTrue(!orderItemList.iterator().hasNext());
    }

    @Test
    public void testFindByProductIdPositive(){
        OrderItem orderItem=new OrderItem();
        orderItem.setProductId(5L);
        orderItem.setQuantity(2);
        orderItem.setPrice(1299);

        Order order=new Order();
        order.setUserId(5L);
        order.setStatus("CANCELLED");
        order.setCreatedAt(LocalDateTime.of(2026,2,5,16,20,0));

        entityManager.persist(order);

        orderItem.setOrder(order);
        entityManager.persist(orderItem);
        Long productId= orderItem.getProductId();

        List<OrderItem> orderItemList = orderItemRepository.findByProductId(productId);
        assertTrue(orderItemList.iterator().hasNext());
    }

    @Test
    public void testFindByProductIdNegative(){
        List<OrderItem> orderItemList = orderItemRepository.findByProductId(5L);
        assertTrue(!orderItemList.iterator().hasNext());
    }

}
