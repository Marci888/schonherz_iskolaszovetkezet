package hu.bme.aut.cart.exception;

import lombok.Getter;

@Getter
public class InsufficientFundsException extends RuntimeException {
    private final String errorCode;
    public InsufficientFundsException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}