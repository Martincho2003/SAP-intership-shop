package com.example.sap_shop.dto;

import java.util.List;

public class ShoppingCartDTO {
    private List<OrderItemDTO> orderItemDTOS;

    public ShoppingCartDTO() {}

    public ShoppingCartDTO(List<OrderItemDTO> orderItemDTOS) {
        this.orderItemDTOS = orderItemDTOS;
    }

    public List<OrderItemDTO> getOrderItemDTOS() {
        return orderItemDTOS;
    }

    public void setOrderItemDTOS(List<OrderItemDTO> orderItemDTOS) {
        this.orderItemDTOS = orderItemDTOS;
    }
}
