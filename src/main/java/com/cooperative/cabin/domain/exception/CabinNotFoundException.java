package com.cooperative.cabin.domain.exception;

public class CabinNotFoundException extends RuntimeException {
    public CabinNotFoundException(String message) {
        super(message);
    }
}
