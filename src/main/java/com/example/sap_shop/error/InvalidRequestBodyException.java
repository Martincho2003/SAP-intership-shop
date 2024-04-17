package com.example.sap_shop.error;

public class InvalidRequestBodyException extends Exception{
    public InvalidRequestBodyException(String message) {
        super(message);
    }
}
