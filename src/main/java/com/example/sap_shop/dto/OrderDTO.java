package com.example.sap_shop.dto;

import java.util.Date;
import java.util.List;

public class OrderDTO {
    private Long id;
    private Date orderDate;
    private String status;
    private List<OrderItemDTO> orderItems;

    public OrderDTO() {
    }

    public OrderDTO(Long id, Date orderDate, String status, List<OrderItemDTO> orderItems) {
        this.id = id;
        this.orderDate = orderDate;
        this.status = status;
        this.orderItems = orderItems;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }
}
