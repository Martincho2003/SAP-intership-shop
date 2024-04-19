package com.example.sap_shop.dto;

public class ProductDTO {

    private String name;
    private String description;
    private Float price;
    private Integer quantity;
    private String imagePath;
    private Float minPrice;
    private String categoryName;

    public ProductDTO() {
    }

    public ProductDTO(String name, String description, Float price, Integer quantity, String imagePath, Float minPrice, String categoryName) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.imagePath = imagePath;
        this.minPrice = minPrice;
        this.categoryName = categoryName;
    }

    public ProductDTO(String name, Integer quantity, Float price, String description) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Float minPrice) {
        this.minPrice = minPrice;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
