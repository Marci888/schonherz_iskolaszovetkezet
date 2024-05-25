package hu.bme.aut.core.exception;

import lombok.Getter;

@Getter
public class CoreServiceException extends RuntimeException {
    private final String errorCode;

    public CoreServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}