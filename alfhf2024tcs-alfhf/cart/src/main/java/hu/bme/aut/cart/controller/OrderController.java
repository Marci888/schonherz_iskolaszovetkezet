package hu.bme.aut.cart.controller;

import hu.bme.aut.cart.dto.OrderDTO;
import hu.bme.aut.cart.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * REST controller for managing orders within the CART module.
 */
@RestController
@RequestMapping("/cart/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    /**
     * Retrieves all orders associated with a specific user.
     * The user ID is extracted from the request header.
     *
     * @param userToken The user token from the request header.
     * @return A list of OrderDTOs representing the user's orders.
     */
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getOrdersByUserToken(@RequestHeader("User-Token") String userToken) {
        log.debug("Received request to fetch orders for user token {}", userToken);
        try {
            List<OrderDTO> orders = orderService.getOrdersByUserToken(userToken);
            log.info("Orders fetched successfully for user token {}", userToken);
            return ResponseEntity.ok(orders);
        } catch (Exception ex) {
            log.error("Error fetching orders for user token {}: {}", userToken, ex.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching orders", ex);
        }
    }

    /**
     * Retrieves a specific order by its ID.
     *
     * @param orderId The ID of the order to retrieve.
     * @return An OrderDTO containing detailed information about the order.
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        log.debug("Received request to fetch order with ID {}", orderId);
        try {
            OrderDTO orderDTO = orderService.getOrderById(orderId);
            log.info("Order fetched successfully with ID {}", orderId);
            return ResponseEntity.ok(orderDTO);
        } catch (Exception ex) {
            log.error("Error retrieving order with ID {}: {}", orderId, ex.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving order", ex);
        }
    }

    /**
     * Creates an order from a specific basket identified by basket ID.
     *
     * @param userToken The user token from the request header.
     * @param basketId The ID of the basket to convert into an order.
     * @param cardId The ID of the card used for the order.
     * @return An OrderDTO representing the newly created order.
     */
    @PostMapping("/{basketId}/{cardId}")
    public ResponseEntity<OrderDTO> createOrderFromBasket(
            @RequestHeader("User-Token") String userToken,
            @PathVariable Long basketId,
            @PathVariable String cardId) {
        log.debug("Received request to create order from basket ID {}", basketId);
        try {
            OrderDTO orderDTO = orderService.createOrderFromBasket(basketId, cardId, userToken);
            log.info("Order created successfully from basket ID {}", basketId);
            return ResponseEntity.ok(orderDTO);
        } catch (IllegalStateException ex) {
            log.error("Failed to create order from basket ID {}: {}", basketId, ex.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception ex) {
            log.error("Error creating order from basket ID {}: {}", basketId, ex.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating order", ex);
        }
    }
}