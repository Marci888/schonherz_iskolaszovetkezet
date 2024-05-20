package hu.bme.aut.cart.model.entity;

import hu.bme.aut.cart.model.enums.BasketStatus;
import hu.bme.aut.cart.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * Entity class for representing an order in the CART module.
 * Each order is directly linked to a basket which contains the selected products.
 */
@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(of = "orderId")
@Slf4j
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basket_id", nullable = false)
    private Basket basket;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate = new Date();

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false)
    private Double totalAmount;

    public void setBasket(Basket basket) {
        if (basket == null) {
            log.error("Attempted to set a null basket for Order ID {}", orderId);
            throw new IllegalArgumentException("Basket cannot be null");
        }

        if (this.basket != null && this.basket != basket) {
            log.info("Replacing existing basket for Order ID {}", orderId);
        }

        this.basket = basket;

        if (basket.getOrder() != this) {
            basket.setOrder(this);
            basket.setBasketStatus(BasketStatus.CHECKED_OUT);
            log.info("Linking back from Basket ID {} to Order ID {}", basket.getBasketId(), orderId);
        }
    }
}