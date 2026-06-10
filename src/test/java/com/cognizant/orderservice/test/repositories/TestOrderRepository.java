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

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = OrderServiceApplication.class)
@ActiveProfiles("test")
public class TestOrderRepository {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TestEntityManager entityManager;

    private Order buildOrder(Long userId, String status, LocalDateTime createdAt) {
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(status);
        order.setCreatedAt(createdAt);
        return order;
    }

    @Test
    void testFindAllPositive() {
        entityManager.persist(buildOrder(1L, "CREATED", LocalDateTime.of(2026, 2, 1, 10, 0, 0)));

        assertFalse(orderRepository.findAll().isEmpty());
    }

    @Test
    void testFindAllNegative() {
        assertTrue(orderRepository.findAll().isEmpty());
    }

    @Test
    void testFindByIdPositive() {
        Order order = buildOrder(2L, "CREATED", LocalDateTime.of(2026, 2, 2, 11, 30, 0));
        entityManager.persist(order);

        assertTrue(orderRepository.findById(order.getId()).isPresent());
    }

    @Test
    void testFindByIdNegative() {
        assertTrue(orderRepository.findById(2L).isEmpty());
    }

    @Test
    void testSavePositive() {
        Order order = buildOrder(3L, "PAID", LocalDateTime.of(2026, 2, 3, 9, 15, 0));
        orderRepository.save(order);

        assertTrue(orderRepository.findById(order.getId()).isPresent());
    }

    @Test
    void testSaveNegative() {
        assertTrue(orderRepository.findById(3L).isEmpty());
    }

    @Test
    void deletePositive() {
        Order order = buildOrder(4L, "SHIPPED", LocalDateTime.of(2026, 2, 4, 14, 45, 0));
        entityManager.persist(order);
        Long id = order.getId();

        orderRepository.delete(order);

        assertTrue(orderRepository.findById(id).isEmpty());
    }

    @Test
    void deleteNegative() {
        assertTrue(orderRepository.findById(4L).isEmpty());
    }

    @Test
    void findByUserIdPositive() {
        Order order = buildOrder(5L, "CANCELLED", LocalDateTime.of(2026, 2, 5, 16, 20, 0));
        entityManager.persist(order);

        assertFalse(orderRepository.findByUserId(order.getUserId()).isEmpty());
    }

    @Test
    void findByUserIdNegative() {
        assertTrue(orderRepository.findByUserId(5L).isEmpty());
    }
}
