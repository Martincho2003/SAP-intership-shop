package com.example.sap_shop.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SaleDto {
    private String name;
    private Date startDate;
    private Date endDate;
    private Integer percentage;
    private List<CategoryDTO> categoryDTOS;

    public SaleDto() {
    }

    public SaleDto(String name, Date startDate, Date endDate, Integer percentage) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.percentage = percentage;
        this.categoryDTOS = new ArrayList<>();
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

    public List<CategoryDTO> getCategoryDTOS() {
        return categoryDTOS;
    }

    public void setCategoryDTOS(List<CategoryDTO> categoryDTOS) {
        this.categoryDTOS = categoryDTOS;
    }
}
