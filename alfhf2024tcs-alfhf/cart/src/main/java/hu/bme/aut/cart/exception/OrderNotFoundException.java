package hu.bme.aut.cart.exception;

import lombok.Getter;

@Getter
public class OrderNotFoundException extends RuntimeException{
    private final String errorCode;

    public OrderNotFoundException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}