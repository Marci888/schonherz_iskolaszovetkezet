package hu.bme.aut.cart.exception;

import hu.bme.aut.cart.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for the CART module.
 */
@ControllerAdvice
public class CartExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Object> handleProductNotFoundException(ProductNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(false, ex.getMessage(), ex.getErrorCode()), HttpStatus.NOT_FOUND);
    }
}