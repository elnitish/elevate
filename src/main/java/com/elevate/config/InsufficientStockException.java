package com.elevate.config;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String productId, int available, int requested) {
        super("Insufficient stock for product " + productId +
                ". Available: " + available + ", Requested: " + requested);
    }
}
