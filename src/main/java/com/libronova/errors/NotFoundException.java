package com.libronova.errors;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String resourceType, Object id) {
        super(resourceType + " with ID " + id + " not found");
    }
}