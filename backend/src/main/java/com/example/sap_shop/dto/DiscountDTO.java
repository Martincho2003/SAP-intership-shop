package com.example.sap_shop.dto;

import java.util.Date;
import java.util.List;

public class DiscountDTO {
    private String name;
    private Date startDate;
    private Date endDate;
    private Integer percentage;
    private List<ProductDTO> productDTOS;

    public DiscountDTO() {
    }

    public DiscountDTO(String name, Date startDate, Date endDate, Integer percentage, List<ProductDTO> productDTOS) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.percentage = percentage;
        this.productDTOS = productDTOS;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    public List<ProductDTO> getProductDTOS() {
        return productDTOS;
    }

    public void setProductDTOS(List<ProductDTO> productDTOS) {
        this.productDTOS = productDTOS;
    }
}
