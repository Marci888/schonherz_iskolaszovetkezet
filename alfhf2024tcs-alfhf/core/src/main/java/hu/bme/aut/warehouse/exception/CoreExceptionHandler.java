package hu.bme.aut.warehouse.exception;

import hu.bme.aut.warehouse.dto.CoreValidationResponseDTO;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Centralized exception handler class for handling exceptions specifically thrown from the CORE module.
 */
@Slf4j
@NoArgsConstructor
@ControllerAdvice
public class CoreExceptionHandler {

    /**
     * Handles exceptions of type CoreServiceException.
     *
     * @param ex the CoreServiceException instance caught by this handler.
     * @return a ResponseEntity object containing the details of the error.
     */
    @ExceptionHandler(CoreServiceException.class)
    public ResponseEntity<CoreValidationResponseDTO> handleCoreServiceException(CoreServiceException ex) {
        log.error("Core module exception: {} - Error Code: {}", ex.getMessage(), ex.getErrorCode());
        CoreValidationResponseDTO errorResponse = new CoreValidationResponseDTO(false, ex.getErrorCode(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
}