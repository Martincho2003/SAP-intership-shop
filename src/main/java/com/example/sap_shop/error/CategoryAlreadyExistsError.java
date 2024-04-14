package com.example.sap_shop.error;

public class CategoryAlreadyExistsError extends Exception {
    public CategoryAlreadyExistsError(String message) {
        super(message);
    }
}
