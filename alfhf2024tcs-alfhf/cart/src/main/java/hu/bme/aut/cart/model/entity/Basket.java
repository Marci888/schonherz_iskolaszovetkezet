package hu.bme.aut.cart.model.entity;

import hu.bme.aut.cart.model.enums.BasketStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Entity class for representing a shopping basket in the CART module.
 * Each basket is linked to a user and can contain multiple products.
 * The basket can have different statuses indicating its current state.
 */
@Entity
@Table(name = "basket")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(of = "basketId")
@Slf4j
public class Basket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long basketId;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BasketStatus basketStatus = BasketStatus.ACTIVE;

    @Column(nullable = false)
    private Double subTotalAmount;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "basket_products", joinColumns = @JoinColumn(name = "basket_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<Long, Integer> products = new HashMap<>();

    @OneToOne(mappedBy = "basket", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Order order;

    /**
     * Adds a product and its quantity to the basket.
     * If the product is already in the basket, it updates the quantity.
     *
     * @param productId the ID of the product to add or update
     * @param price the price of a piece
     * @param quantity the quantity of the product
     */
    public void addProduct(Long productId, Double price, Integer quantity) {
        Integer currentQuantity = products.getOrDefault(productId, 0);
        products.put(productId, currentQuantity + quantity);
        subTotalAmount += quantity * price;
        log.info("Updated product {} in basket {}, new quantity: {}, new subtotal: {}", productId, basketId, products.get(productId), subTotalAmount);
    }

    /**
     * Removes a specific quantity of a product from the basket. If the quantity to remove
     * equals or exceeds the current quantity, the product is completely removed.
     *
     * @param productId the ID of the product to remove
     * @param price the price per unit of the product
     * @param quantity the quantity to remove
     * @throws IllegalArgumentException if the product id does not exist or the quantity is invalid
     */
    public void removeProduct(Long productId, Double price, Integer quantity) {
        Integer currentQuantity = products.get(productId);
        if (currentQuantity == null || currentQuantity < quantity) {
            log.error("Attempted to remove non-existing or insufficient quantity of product {}: requested {}, available {}", productId, quantity, currentQuantity);
            throw new IllegalArgumentException("Product not found or insufficient quantity.");
        }

        if (currentQuantity.equals(quantity)) {
            products.remove(productId);
        } else {
            products.put(productId, currentQuantity - quantity);
        }

        subTotalAmount -= quantity * price;
        log.info("Removed product {} from basket {}, removed quantity: {}, new quantity: {}, new subtotal: {}", productId, basketId, quantity, products.get(productId), subTotalAmount);
    }
}