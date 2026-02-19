package com.cognizant.orderservice.test.repositories;

import com.cognizant.orderservice.entities.Order;
import com.cognizant.orderservice.main.OrderServiceApplication;
import com.cognizant.orderservice.repositories.OrderRepository;
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
public class TestOrderRepository {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testFindAllPositive(){
        Order order=new Order();
        order.setUserId(1L);
        order.setStatus("CREATED");
        order.setCreatedAt(LocalDateTime.of(2026,2,1,10,0,0));

        entityManager.persist(order);

        List<Order> orderList = orderRepository.findAll();
        assertTrue(orderList.iterator().hasNext());
    }

    @Test
    public void testFindAllNegative(){
        List<Order> orderList = orderRepository.findAll();
        assertTrue(!orderList.iterator().hasNext());
    }

    @Test
    public void testFindByIdPositive(){
        Order order=new Order();
        order.setUserId(2L);
        order.setStatus("CREATED");
        order.setCreatedAt(LocalDateTime.of(2026,2,2,11,30,0));

        entityManager.persist(order);
        Long id=order.getId();

        Optional<Order> orderOptional=orderRepository.findById(id);
        assertTrue(orderOptional.isPresent());
    }

    @Test
    public void testFindByIdNegative(){
        Optional<Order> orderOptional=orderRepository.findById(2L);
        assertTrue(!orderOptional.isPresent());
    }

    @Test
    public void testSavePositive(){
        Order order=new Order();
        order.setUserId(3L);
        order.setStatus("PAID");
        order.setCreatedAt(LocalDateTime.of(2026,2,3,9,15,0));

        orderRepository.save(order);
        Long id=order.getId();

        Optional<Order> orderOptional=orderRepository.findById(id);
        assertTrue(orderOptional.isPresent());
    }

    @Test
    public void testSaveNegative(){
        Optional<Order> orderOptional=orderRepository.findById(3L);
        assertTrue(!orderOptional.isPresent());
    }

    @Test
    public void deletePositive(){
        Order order=new Order();
        order.setUserId(4L);
        order.setStatus("SHIPPED");
        order.setCreatedAt(LocalDateTime.of(2026,2,4,14,45,0));

        entityManager.persist(order);
        Long id=order.getId();

        orderRepository.delete(order);
        Optional<Order> orderOptional=orderRepository.findById(id);
        assertTrue(!orderOptional.isPresent());
    }

    @Test
    public void deleteNegative(){
        Optional<Order> orderOptional=orderRepository.findById(4L);
        assertTrue(!orderOptional.isPresent());
    }

    @Test
    public void findByUserIdPositive(){
        Order order=new Order();
        order.setUserId(5L);
        order.setStatus("CANCELLED");
        order.setCreatedAt(LocalDateTime.of(2026,2,5,16,20,0));

        entityManager.persist(order);
        Long userId=order.getUserId();

        List<Order> orderList=orderRepository.findByUserId(userId);
        assertTrue(orderList.iterator().hasNext());
    }

    @Test
    public void findByUserIdNegative(){
        List<Order> orderList=orderRepository.findByUserId(5L);
        assertTrue(!orderList.iterator().hasNext());
    }
}
