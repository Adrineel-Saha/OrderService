package com.cognizant.orderservice.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="Orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Order_Id")
    private Long id;

    @Column(name="User_Id")
    private Long userId;

    @Column(name="Status")
    private String status;

    @Column(name="Created_At")
    private LocalDateTime createdAt;

}
