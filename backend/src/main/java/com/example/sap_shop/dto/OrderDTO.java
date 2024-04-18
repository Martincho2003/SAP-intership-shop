package com.example.sap_shop.dto;

import java.util.Date;
import java.util.List;

public class OrderDTO {
    private Date orderDate;
    private String status;
    private List<OrderItemDTO> orderItems;
    private Float totalPrice;

    public OrderDTO() {
    }

    public OrderDTO(Date orderDate, String status, List<OrderItemDTO> orderItems, Float totalPrice) {
        this.orderDate = orderDate;
        this.status = status;
        this.orderItems = orderItems;
        this.totalPrice = totalPrice;
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

    public Float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Float totalPrice) {
        this.totalPrice = totalPrice;
    }
}
