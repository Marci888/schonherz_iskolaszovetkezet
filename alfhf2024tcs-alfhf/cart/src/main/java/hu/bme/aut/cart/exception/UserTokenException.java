package hu.bme.aut.cart.exception;

import lombok.Getter;

@Getter
public class UserTokenException extends RuntimeException {
    private final String errorCode;

    public UserTokenException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}