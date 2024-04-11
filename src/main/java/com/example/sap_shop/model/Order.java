package com.example.sap_shop.model;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate = new Date();

    @Column(nullable = false)
    private String status;

    @OneToMany()
    private List<OrderItem> orderItems;

}
