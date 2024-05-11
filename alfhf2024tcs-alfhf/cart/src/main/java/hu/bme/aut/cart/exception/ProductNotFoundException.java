package hu.bme.aut.cart.exception;

import lombok.Getter;

/**
 * Custom exception for product not found errors.
 */
@Getter
public class ProductNotFoundException extends RuntimeException {
    private final String errorCode;

    public ProductNotFoundException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}