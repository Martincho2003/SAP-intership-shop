package com.example.sap_shop.dto;

import java.util.List;

public class UserDto {

    private String username;
    private String email;
    private String password;
    private ShoppingCartDTO shoppingCartDTO;
    private List<OrderDTO> orderDTOS;

    public UserDto() {
    }


    public UserDto(String username, String email, ShoppingCartDTO shoppingCartDTO, List<OrderDTO> orderDTOS) {
        this.username = username;
        this.email = email;
        this.shoppingCartDTO = shoppingCartDTO;
        this.orderDTOS = orderDTOS;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ShoppingCartDTO getShoppingCartDTO() {
        return shoppingCartDTO;
    }

    public void setShoppingCartDTO(ShoppingCartDTO shoppingCartDTO) {
        this.shoppingCartDTO = shoppingCartDTO;
    }

    public List<OrderDTO> getOrderDTOS() {
        return orderDTOS;
    }

    public void setOrderDTOS(List<OrderDTO> orderDTOS) {
        this.orderDTOS = orderDTOS;
    }
}