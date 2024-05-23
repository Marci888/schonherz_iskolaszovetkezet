package hu.bme.aut.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for encapsulating validation responses from the CORE module.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoreValidationResponseDTO {

    /**
     * Indicates whether the validation was successful.
     */
    private boolean success;

    /**
     * The error code provided if the validation fails.
     */
    private String errorCode;

    /**
     * The error message associated with the error code.
     */
    private String errorMessage;
}
