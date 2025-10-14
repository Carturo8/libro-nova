package com.libronova.errors;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String bookTitle) {
        super("Book '" + bookTitle + "' has no available copies for loan");
    }

    public InsufficientStockException(String bookTitle, int availableCopies) {
        super("Book '" + bookTitle + "' has only " + availableCopies + " copies available");
    }
}