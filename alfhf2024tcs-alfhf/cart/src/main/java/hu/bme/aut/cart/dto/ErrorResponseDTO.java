package hu.bme.aut.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ErrorResponseDTO {
    private Boolean success;
    private String errorMessage;
    private String errorCode;
}