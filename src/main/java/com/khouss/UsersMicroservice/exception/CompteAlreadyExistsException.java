package com.khouss.UsersMicroservice.exception;

public class CompteAlreadyExistsException extends RuntimeException {
    public CompteAlreadyExistsException(String message) {
        super(message);
    }
}

