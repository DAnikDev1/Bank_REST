package com.example.bankcards.exception;

public class DataValidationException extends RuntimeException{
    public DataValidationException(String message) {
        super(message);
    }
}
