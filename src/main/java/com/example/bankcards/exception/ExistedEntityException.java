package com.example.bankcards.exception;

public class ExistedEntityException extends RuntimeException {
    public ExistedEntityException(String message) {
        super(message);
    }
}
