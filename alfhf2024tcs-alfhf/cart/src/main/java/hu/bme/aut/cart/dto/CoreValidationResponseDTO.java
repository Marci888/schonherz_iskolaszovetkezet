package hu.bme.aut.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoreValidationResponseDTO {
    private boolean success;
    private String errorCode;
    private String errorMessage;
}