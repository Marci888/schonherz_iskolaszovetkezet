package hu.bme.aut.cart.exception;

import lombok.Getter;

@Getter
public class BasketNotFoundException extends RuntimeException{
    private final String errorCode;

    public BasketNotFoundException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
