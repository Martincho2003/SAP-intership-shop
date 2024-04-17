package com.example.sap_shop.dto;

import java.util.ArrayList;
import java.util.List;

public class CategoryDTO {
    private String name;
    private List<ProductDTO> productDTOS;

    public CategoryDTO() {
    }

    public CategoryDTO(String name, List<ProductDTO> productDTOS) {
        this.name = name;
        this.productDTOS = productDTOS;
    }

    public CategoryDTO(String name) {

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
