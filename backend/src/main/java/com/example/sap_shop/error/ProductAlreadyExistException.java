package com.example.sap_shop.error;

public class ProductAlreadyExistException extends Exception{
    public ProductAlreadyExistException(String message) {
        super(message);
    }
}
