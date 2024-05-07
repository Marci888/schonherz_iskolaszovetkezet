package hu.bme.aut.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductDTO {
    private Long id;
    private String name;
    private String category;
    private Double price;
    private Integer quantity;
}