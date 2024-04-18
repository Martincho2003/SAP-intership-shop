package com.example.sap_shop.error;

public class FieldCannotBeEmptyException extends Exception {
    public FieldCannotBeEmptyException(String message) {
        super(message);
    }
}
