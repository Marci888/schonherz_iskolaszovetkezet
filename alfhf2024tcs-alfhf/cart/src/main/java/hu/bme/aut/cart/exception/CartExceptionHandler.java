package hu.bme.aut.cart.exception;

import hu.bme.aut.cart.dto.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for the CART module
 */
@ControllerAdvice
@Slf4j
public class CartExceptionHandler {

    /**
     * Handles cases where a product specified in the request is not found.
     *
     * @param ex the caught ProductNotFoundException
     * @return a ResponseEntity containing an ErrorResponseDTO with error details
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleProductNotFoundException(ProductNotFoundException ex) {
        log.error("Product not found: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponseDTO(false, ex.getMessage(), ex.getErrorCode()), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles cases where a basket specified in the request is not found.
     *
     * @param ex the caught BasketNotFoundException
     * @return a ResponseEntity containing an ErrorResponseDTO with error details
     */
    @ExceptionHandler(BasketNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleBasketNotFoundException(BasketNotFoundException ex) {
        log.error("Basket not found: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponseDTO(false, ex.getMessage(), ex.getErrorCode()), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles exceptions resulting from failed communication with external services.
     *
     * @param ex the caught ServiceCommunicationException
     * @return a ResponseEntity containing an ErrorResponseDTO with error details
     */
    @ExceptionHandler(ServiceCommunicationException.class)
    public ResponseEntity<ErrorResponseDTO> handleServiceCommunicationException(ServiceCommunicationException ex) {
        log.error("Service communication error: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponseDTO(false, ex.getMessage(), ex.getErrorCode()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles cases where an order specified in the request is not found.
     *
     * @param ex the caught OrderNotFoundException
     * @return a ResponseEntity containing an ErrorResponseDTO with error details
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleOrderNotFoundException(OrderNotFoundException ex) {
        log.error("Order not found: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponseDTO(false, ex.getMessage(), ex.getErrorCode()), HttpStatus.NOT_FOUND);
    }
}