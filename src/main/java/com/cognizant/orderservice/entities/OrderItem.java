package com.cognizant.orderservice.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="Order_Items")
@Data
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

}
