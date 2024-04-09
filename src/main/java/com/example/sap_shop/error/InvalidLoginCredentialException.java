package com.example.sap_shop.error;

public class InvalidLoginCredentialException extends Exception{
    public InvalidLoginCredentialException(String message) {
        super(message);
    }
}
