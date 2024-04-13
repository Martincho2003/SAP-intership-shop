package com.example.sap_shop.error;

public class FieldCannotBeEmptyException extends Throwable {
    public FieldCannotBeEmptyException(String message) {
        super(message);
    }
}
