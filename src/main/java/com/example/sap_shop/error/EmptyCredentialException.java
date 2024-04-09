package com.example.sap_shop.error;

public class EmptyCredentialException extends Exception{
    public EmptyCredentialException() {
        super("Some of the credentials are empty");
    }
}
