package hu.bme.aut.cart.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import hu.bme.aut.cart.dto.BasketDTO;
import hu.bme.aut.cart.dto.ErrorResponseDTO;
import hu.bme.aut.cart.service.BasketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing baskets within the CART module.
 */
@RestController
@RequestMapping("/cart/basket")
@RequiredArgsConstructor
@Slf4j
public class BasketController {

    private final BasketService basketService;

    @GetMapping
    public ResponseEntity<?> getBasketByUser(@RequestHeader("User-Token") String userToken ) {
        log.debug("Fetching basket for user");
        BasketDTO basketDTO = basketService.getBasketByUser(userToken);
        log.info("Basket fetched successfully with ID {}", basketDTO.getBasketId());
        return ResponseEntity.ok(basketDTO);
    }

    /**
     * Retrieves a basket by its ID.
     *
     * @param basketId the ID of the basket to retrieve.
     * @return a ResponseEntity containing the BasketDTO or an error response.
     */
    @GetMapping("/{basketId}")
    public ResponseEntity<?> getBasketById(@PathVariable Long basketId) {
        log.debug("Fetching basket with ID {}", basketId);
        BasketDTO basketDTO = basketService.getBasketById(basketId);
        log.info("Basket fetched successfully with ID {}", basketId);
        return ResponseEntity.ok(basketDTO);
    }

    /**
     * Adds a product to the active basket for a given user.
     *
     * @param userToken the user token from the request header.
     * @param productId the product ID to add.
     * @param quantity the quantity of the product to add.
     * @return a ResponseEntity containing the updated BasketDTO or an error response.
     */
    @PutMapping("/{productId}/{quantity}")
    public ResponseEntity<?> addToBasket(@RequestHeader("User-Token") String userToken,
                                         @PathVariable Long productId,
                                         @PathVariable Integer quantity) {
        try {
            log.debug("Adding product {} with quantity {} to basket", productId, quantity);
            BasketDTO basketDTO = basketService.addToBasket(userToken, productId, quantity);
            log.info("Product {} with quantity {} added to basket", productId, quantity);
            return ResponseEntity.ok(basketDTO);
        } catch (IllegalArgumentException e) {
            log.error("Error adding product to basket: {}", e.getMessage());
            return new ResponseEntity<>(new ErrorResponseDTO(false, e.getMessage(), "3406"), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Removes a product from the active basket for a given user.
     *
     * @param userToken the user token from the request header.
     * @param productId the product ID to remove.
     * @param quantity the quantity of the product to remove.
     * @return a ResponseEntity containing the updated BasketDTO or an error response.
     */
    @DeleteMapping("/{productId}/{quantity}")
    public ResponseEntity<?> removeFromBasket(@RequestHeader("User-Token") String userToken,
                                              @PathVariable Long productId,
                                              @PathVariable Integer quantity) {
        log.debug("Removing product {} with quantity {} from basket", productId, quantity);
        BasketDTO basketDTO = basketService.removeFromBasket(userToken, productId, quantity);
        log.info("Product {} with quantity {} removed from basket", productId, quantity);
        return ResponseEntity.ok(basketDTO);
    }
}