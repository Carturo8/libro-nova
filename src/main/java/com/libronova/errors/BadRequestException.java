package com.libronova.errors;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String field, String reason) {
        super("Invalid " + field + ": " + reason);
    }
}