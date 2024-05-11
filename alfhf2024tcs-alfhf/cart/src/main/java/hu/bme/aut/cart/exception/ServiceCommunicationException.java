package hu.bme.aut.cart.exception;

import lombok.Getter;

@Getter
public class ServiceCommunicationException extends RuntimeException {
    private final String errorCode;

    public ServiceCommunicationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}