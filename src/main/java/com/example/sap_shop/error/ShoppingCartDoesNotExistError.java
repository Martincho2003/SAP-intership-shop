package com.example.sap_shop.error;

public class ShoppingCartDoesNotExistError extends Exception {
    public ShoppingCartDoesNotExistError(String message) {
        super(message);
    }
}
