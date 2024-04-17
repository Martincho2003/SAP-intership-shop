package com.example.sap_shop.error;

public class CategoryNotFoundException extends Exception {
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
