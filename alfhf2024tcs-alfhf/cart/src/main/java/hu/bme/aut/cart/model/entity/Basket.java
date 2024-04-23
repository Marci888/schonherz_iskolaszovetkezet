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
    private BasketStatus basketStatus;

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
     * @param quantity the quantity of the product
     */
    public void addProduct(Long productId, Integer quantity) {
        if (products.containsKey(productId)) {
            products.put(productId, products.get(productId) + quantity);
            log.info("Quantity updated for product {}: {}", productId, products.get(productId));
        } else {
            products.put(productId, quantity);
            log.info("Product added {} with quantity {}", productId, quantity);
        }
    }

    /**
     * Removes a product from the basket.
     *
     * @param productId the ID of the product to remove
     * @throws IllegalArgumentException if the product id does not exist
     */
    public void removeProduct(Long productId) {
        if (products.containsKey(productId)) {
            products.remove(productId);
            log.info("Removed product {}", productId);
        } else {
            log.error("Attempted to remove non-existing product {}", productId);
            throw new IllegalArgumentException("Product not found.");
        }
    }
}