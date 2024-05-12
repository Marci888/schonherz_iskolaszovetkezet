package hu.bme.aut.cart.controller;

import hu.bme.aut.cart.dto.BasketDTO;
import hu.bme.aut.cart.dto.ErrorResponseDTO;
import hu.bme.aut.cart.service.BasketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing baskets within the CART module.
 */
@RestController
@RequestMapping("/cart/basket")
@RequiredArgsConstructor
@Slf4j
public class BasketController {

    private final BasketService basketService;

    /**
     * Retrieves a basket by its ID.
     * @param basketId the ID of the basket to retrieve.
     * @return a ResponseEntity containing the BasketDTO or an error response.
     */
    @GetMapping("/{basketId}")
    public ResponseEntity<?> getBasketById(@PathVariable Long basketId) {
        log.debug("Fetching basket with ID {}", basketId);
        BasketDTO basketDTO = basketService.getBasketById(basketId);
        return ResponseEntity.ok(basketDTO);
    }

    /**
     * Adds a product to the active basket for a given user.
     * @param userId the user ID from the request header.
     * @param productId the product ID to add.
     * @param quantity the quantity of the product to add.
     * @return a ResponseEntity containing the updated BasketDTO or an error response.
     */
    @PutMapping("/{productId}/{quantity}")
    public ResponseEntity<?> addToBasket(@RequestHeader("User-Id") Long userId,
                                         @PathVariable Long productId,
                                         @PathVariable Integer quantity) {
        try {
            log.debug("Adding product {} with quantity {} to user {}'s basket", productId, quantity, userId);
            BasketDTO basketDTO = basketService.addToBasket(userId, productId, quantity);
            return ResponseEntity.ok(basketDTO);
        } catch (IllegalArgumentException e) {
            log.error("Error adding product to basket: {}", e.getMessage());
            return new ResponseEntity<>(new ErrorResponseDTO(false, e.getMessage(), "3406"), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Removes a product from the active basket for a given user.
     * @param userId the user ID from the request header.
     * @param productId the product ID to remove.
     * @param quantity the quantity of the product to remove.
     * @return a ResponseEntity containing the updated BasketDTO or an error response.
     */
    @DeleteMapping("/{productId}/{quantity}")
    public ResponseEntity<?> removeFromBasket(@RequestHeader("User-Id") Long userId,
                                              @PathVariable Long productId,
                                              @PathVariable Integer quantity) {
        log.debug("Removing product {} with quantity {} from user {}'s basket", productId, quantity, userId);
        BasketDTO basketDTO = basketService.removeFromBasket(userId, productId, quantity);
        return ResponseEntity.ok(basketDTO);
    }
}