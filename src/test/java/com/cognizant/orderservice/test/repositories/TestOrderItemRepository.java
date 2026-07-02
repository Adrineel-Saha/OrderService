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

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = OrderServiceApplication.class)
@ActiveProfiles("test")
class TestOrderItemRepository {

    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private TestEntityManager entityManager;

    private Order persistOrder(Long userId, String status, LocalDateTime createdAt) {
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(status);
        order.setCreatedAt(createdAt);
        entityManager.persist(order);
        return order;
    }

    private OrderItem buildItem(Long productId, int quantity, double price, Order order) {
        OrderItem item = new OrderItem();
        item.setProductId(productId);
        item.setQuantity(quantity);
        item.setPrice(price);
        item.setOrder(order);
        return item;
    }

    @Test
    void testFindAllPositive() {
        Order order = persistOrder(1L, "CREATED", LocalDateTime.of(2026, 2, 1, 10, 0, 0));
        entityManager.persist(buildItem(1L, 2, 499.5, order));

        assertFalse(orderItemRepository.findAll().isEmpty());
    }

    @Test
    void testFindAllNegative() {
        assertTrue(orderItemRepository.findAll().isEmpty());
    }

    @Test
    void testFindByIdPositive() {
        Order order = persistOrder(2L, "CREATED", LocalDateTime.of(2026, 2, 2, 11, 30, 0));
        OrderItem item = buildItem(2L, 1, 1299, order);
        entityManager.persist(item);

        assertTrue(orderItemRepository.findById(item.getId()).isPresent());
    }

    @Test
    void testFindByIdNegative() {
        assertTrue(orderItemRepository.findById(2L).isEmpty());
    }

    @Test
    void testSavePositive() {
        Order order = persistOrder(3L, "PAID", LocalDateTime.of(2026, 2, 3, 9, 15, 0));
        OrderItem item = buildItem(3L, 3, 199.99, order);
        orderItemRepository.save(item);

        assertTrue(orderItemRepository.findById(item.getId()).isPresent());
    }

    @Test
    void testSaveNegative() {
        assertTrue(orderItemRepository.findById(3L).isEmpty());
    }

    @Test
    void deletePositive() {
        Order order = persistOrder(4L, "SHIPPED", LocalDateTime.of(2026, 2, 4, 14, 45, 0));
        OrderItem item = buildItem(4L, 1, 2499, order);
        entityManager.persist(item);
        Long id = item.getId();

        orderItemRepository.delete(item);

        assertTrue(orderItemRepository.findById(id).isEmpty());
    }

    @Test
    void deleteNegative() {
        assertTrue(orderItemRepository.findById(4L).isEmpty());
    }

    @Test
    void testFindByOrderIdPositive() {
        Order order = persistOrder(5L, "CANCELLED", LocalDateTime.of(2026, 2, 5, 16, 20, 0));
        entityManager.persist(buildItem(5L, 2, 1299, order));

        assertFalse(orderItemRepository.findByOrderId(order.getId()).isEmpty());
    }

    @Test
    void testFindByOrderIdNegative() {
        assertTrue(orderItemRepository.findByOrderId(5L).isEmpty());
    }

    @Test
    void testFindByProductIdPositive() {
        Order order = persistOrder(5L, "CANCELLED", LocalDateTime.of(2026, 2, 5, 16, 20, 0));
        OrderItem item = buildItem(5L, 2, 1299, order);
        entityManager.persist(item);

        assertFalse(orderItemRepository.findByProductId(item.getProductId()).isEmpty());
    }

    @Test
    void testFindByProductIdNegative() {
        assertTrue(orderItemRepository.findByProductId(5L).isEmpty());
    }
}
