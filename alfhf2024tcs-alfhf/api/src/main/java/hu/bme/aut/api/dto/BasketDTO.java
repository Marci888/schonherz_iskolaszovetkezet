package hu.bme.aut.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object for basket details in the API module.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketDTO {
    private Long basketId;
    private String basketStatus;
    private Double subtotalAmount;
    private List<ProductDTO> products;
}