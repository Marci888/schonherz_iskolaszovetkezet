package hu.bme.aut.cart.dto;

import hu.bme.aut.cart.model.enums.BasketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketDTO {
    private boolean success;
    private Long basketId;
    private BasketStatus basketStatus;
    private Double subtotalAmount;
    private List<ProductDTO> products;
}