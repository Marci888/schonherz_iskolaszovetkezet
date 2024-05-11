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

    @ExceptionHandler(BasketNotFoundException.class)
    public ResponseEntity<Object> handleBasketNotFoundException(ProductNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(false, ex.getMessage(), ex.getErrorCode()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ServiceCommunicationException.class)
    public ResponseEntity<Object> handleServiceCommunicationException(ServiceCommunicationException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(false, ex.getMessage(), ex.getErrorCode());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Object> handleOrderNotFoundException(ProductNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponseDTO(false, ex.getMessage(), ex.getErrorCode()), HttpStatus.NOT_FOUND);
    }
}