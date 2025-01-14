package hu.bme.aut.api.controller;

import hu.bme.aut.api.dto.ApiResponse;
import hu.bme.aut.api.dto.OrderDTO;
import hu.bme.aut.api.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    /**
     * Retrieves all orders associated with a specific user.
     * The user ID is extracted from the request header.
     *
     * @param userToken The user token from the request header.
     * @return ResponseEntity containing ApiResponse with a list of OrderDTOs.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getOrdersByUserId(@RequestHeader("User-Token") String userToken) {
        log.info("Received request to fetch orders");
        try {
            CompletableFuture<ApiResponse<List<OrderDTO>>> future = orderService.getOrdersByUserToken(userToken);
            ApiResponse<List<OrderDTO>> response = future.get();
            log.debug("Successfully retrieved orders.");
            return ResponseEntity.ok(response);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error fetching orders: {}", e.getMessage());
            ApiResponse<List<OrderDTO>> errorResponse = ApiResponse.<List<OrderDTO>>builder()
                    .success(false)
                    .errorMessage("Internal server error")
                    .errorCode("1500")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Retrieves a specific order by its ID.
     *
     * @param orderId The ID of the order to retrieve.
     * @return ResponseEntity containing ApiResponse with the OrderDTO.
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(@PathVariable Long orderId) {
        log.info("Received request to fetch order with ID {}", orderId);
        try {
            CompletableFuture<ApiResponse<OrderDTO>> future = orderService.getOrderById(orderId);
            ApiResponse<OrderDTO> response = future.get();
            log.debug("Successfully retrieved order with ID {}", orderId);
            return ResponseEntity.ok(response);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error retrieving order with ID {}: {}", orderId, e.getMessage());
            ApiResponse<OrderDTO> errorResponse = ApiResponse.<OrderDTO>builder()
                    .success(false)
                    .errorMessage("Internal server error")
                    .errorCode("1500")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Creates an order for a user with the specified card ID.
     *
     * @param userToken The user token from the request header.
     * @param cardId The ID of the card used for the order.
     * @return ResponseEntity containing the created OrderDTO or an error message.
     */
    @PostMapping("/{cardId}")
    public CompletableFuture<ResponseEntity<?>> createOrderForUser(@RequestHeader("User-Token") String userToken,
                                                                   @PathVariable String cardId) {
        log.info("Request to create order for user with card ID: {}", cardId);
        return orderService.createOrderForUser(userToken, cardId)
                .thenApply(response -> {
                    if (response.isSuccess() && response.getData() != null) {
                        log.info("Order created successfully: {}", response.getData());
                        return ResponseEntity.ok(response.getData());
                    } else {
                        log.error("Failed to create order: {}", response.getErrorMessage());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response.getErrorMessage());
                    }
                });
    }
}