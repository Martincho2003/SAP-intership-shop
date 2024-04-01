package com.example.sap_shop.model;

import jakarta.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Order {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // Assuming you have a Customer entity

    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate = new Date();

    private String status;

    // Using LAZY fetching and a Set to avoid duplicates
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<OrderItem> orderItems = new HashSet<>();

    // Standard constructors, getters, and setters

    // Utility method to add an order item to an order
    public void addOrderItem(OrderItem item) {
        this.orderItems.add(item);
        item.setOrder(this);
    }

    // Utility method to remove an order item from an order
    public void removeOrderItem(OrderItem item) {
        this.orderItems.remove(item);
        item.setOrder(null);
    }
}
