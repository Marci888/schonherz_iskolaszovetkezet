package hu.bme.aut.api.controller;

import hu.bme.aut.api.dto.ApiResponse;
import hu.bme.aut.api.dto.BasketDTO;
import hu.bme.aut.api.service.BasketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/basket")
@RequiredArgsConstructor
@Slf4j
public class BasketController {

    private final BasketService basketService;

    /**
     * Retrieves a basket by its ID.
     *
     * @param basketId the ID of the basket to retrieve.
     * @return ResponseEntity containing the ApiResponse with the BasketDTO.
     */
    @GetMapping("/{basketId}")
    public ResponseEntity<ApiResponse<BasketDTO>> getBasketById(@PathVariable Long basketId) {
        log.info("Request to retrieve basket with ID: {}", basketId);
        try {
            CompletableFuture<ApiResponse<BasketDTO>> future = basketService.getBasketById(basketId);
            ApiResponse<BasketDTO> response = future.get();
            log.debug("Basket retrieved successfully for ID: {}", basketId);
            return ResponseEntity.ok(response);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error retrieving basket with ID {}: {}", basketId, e.getMessage());
            ApiResponse<BasketDTO> errorResponse = ApiResponse.<BasketDTO>builder()
                    .success(false)
                    .errorMessage("Internal server error")
                    .errorCode("1500")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Adds a product to the active basket for a given user.
     *
     * @param userId the user ID from the request header.
     * @param productId the product ID to add.
     * @param quantity the quantity of the product to add.
     * @return ResponseEntity containing ApiResponse with the updated BasketDTO.
     */
    @PutMapping("/{productId}/{quantity}")
    public ResponseEntity<ApiResponse<BasketDTO>> addToBasket(@RequestHeader("User-Id") Long userId,
                                                              @PathVariable Long productId,
                                                              @PathVariable Integer quantity) {
        log.info("Adding product {} with quantity {} to basket for user {}", productId, quantity, userId);
        try {
            CompletableFuture<ApiResponse<BasketDTO>> future = basketService.addToBasket(userId, productId, quantity);
            ApiResponse<BasketDTO> response = future.get();
            log.debug("Product added successfully to basket for user {}", userId);
            return ResponseEntity.ok(response);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Failed to add product {} to basket for user {}: {}", productId, userId, e.getMessage());
            ApiResponse<BasketDTO> errorResponse = ApiResponse.<BasketDTO>builder()
                    .success(false)
                    .errorMessage("Failed to add product to basket")
                    .errorCode("1500")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Removes a product from the active basket for a given user.
     *
     * @param userId the user ID from the request header.
     * @param productId the product ID to remove.
     * @param quantity the quantity of the product to remove.
     * @return ResponseEntity containing ApiResponse with the updated BasketDTO.
     */
    @DeleteMapping("/{productId}/{quantity}")
    public ResponseEntity<ApiResponse<BasketDTO>> removeFromBasket(@RequestHeader("User-Id") Long userId,
                                                                   @PathVariable Long productId,
                                                                   @PathVariable Integer quantity) {
        log.info("Removing product {} with quantity {} from basket for user {}", productId, quantity, userId);
        try {
            CompletableFuture<ApiResponse<BasketDTO>> future = basketService.removeFromBasket(userId, productId, quantity);
            ApiResponse<BasketDTO> response = future.get();
            log.debug("Product removed successfully from basket for user {}", userId);
            return ResponseEntity.ok(response);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Failed to remove product {} from basket for user {}: {}", productId, userId, e.getMessage());
            ApiResponse<BasketDTO> errorResponse = ApiResponse.<BasketDTO>builder()
                    .success(false)
                    .errorMessage("Failed to remove product from basket")
                    .errorCode("1500")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}