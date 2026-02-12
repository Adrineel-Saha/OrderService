package com.cognizant.orderservice.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="Order_Items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Order_Item_Id")
    private Long id;

    @Column(name="Product_Id")
    private Long productId;

    @Column(name="Quantity")
    private int quantity;

    @Column(name="Price")
    private double price;

    @ManyToOne
    @JoinColumn(name="Order_Id",referencedColumnName = "Order_Id")
    private Order order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
