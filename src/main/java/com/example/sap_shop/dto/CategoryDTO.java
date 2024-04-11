package com.example.sap_shop.dto;

import java.util.ArrayList;
import java.util.List;

public class CategoryDTO {
    private String name;
    private List<ProductDTO> productDTOS;

    public CategoryDTO() {
    }

    public CategoryDTO(String name) {
        this.name = name;
        this.productDTOS = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProductDTO> getProductDTOS() {
        return productDTOS;
    }

    public void setProductDTOS(List<ProductDTO> productDTOS) {
        this.productDTOS = productDTOS;
    }
}
