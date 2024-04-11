package com.example.sap_shop.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(length = 500)
    private String imagePath;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private Float price;

    @Column(nullable = false)
    private Integer quantity;

    @OneToMany(mappedBy = "", fetch = FetchType.LAZY)
    private List<OrderItem> orderItem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public List<OrderItem> getOrderItem() {
        return orderItem;
    }

    public void setOrderItems(List<OrderItem> orderItem) {
        this.orderItem = orderItem;
    }
}
