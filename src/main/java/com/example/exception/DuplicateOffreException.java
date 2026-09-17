package com.example.exception;

public class DuplicateOffreException extends RuntimeException {
    public DuplicateOffreException(String message) {
        super(message);
    }
}