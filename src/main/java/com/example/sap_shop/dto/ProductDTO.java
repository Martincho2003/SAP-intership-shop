package com.example.sap_shop.dto;

public class ProductDTO {
    private String name;
    private Float price;
    private Integer quantity;
    private String imagePath;


    public ProductDTO() {
    }

    public ProductDTO(String name, Float price, Integer quantity, String imagePath) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
