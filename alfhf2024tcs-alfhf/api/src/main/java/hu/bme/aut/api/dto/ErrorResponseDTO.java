package hu.bme.aut.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for handling errors from CART module..
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {

    /**
     * Indicates whether the operation was successful. Always false for error responses.
     */
    private Boolean success;

    /**
     * A user-friendly message describing the error.
     */
    private String errorMessage;

    /**
     * A specific code associated with the error, useful for client-side error handling and localization.
     */
    private String errorCode;
}