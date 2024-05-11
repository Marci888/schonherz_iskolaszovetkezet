package hu.bme.aut.cart.dto;

import hu.bme.aut.cart.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long orderId;
    private Date orderDate;
    private OrderStatus orderStatus;
    private Double totalAmount;
    private BasketDTO basket;
    private boolean success;
}