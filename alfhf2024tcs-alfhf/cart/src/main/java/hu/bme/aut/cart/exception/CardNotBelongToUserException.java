package hu.bme.aut.cart.exception;

import lombok.Getter;

@Getter
public class CardNotBelongToUserException extends RuntimeException {
    private final String errorCode;
    public CardNotBelongToUserException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}