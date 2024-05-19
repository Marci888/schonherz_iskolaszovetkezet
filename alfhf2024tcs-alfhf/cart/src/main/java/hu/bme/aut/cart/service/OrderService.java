package hu.bme.aut.cart.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import hu.bme.aut.cart.client.CoreClient;
import hu.bme.aut.cart.dto.BasketDTO;
import hu.bme.aut.cart.dto.CoreValidationResponseDTO;
import hu.bme.aut.cart.dto.OrderDTO;
import hu.bme.aut.cart.exception.CardNotBelongToUserException;
import hu.bme.aut.cart.exception.InsufficientFundsException;
import hu.bme.aut.cart.exception.OrderNotFoundException;
import hu.bme.aut.cart.model.entity.Basket;
import hu.bme.aut.cart.model.entity.Order;
import hu.bme.aut.cart.model.enums.OrderStatus;
import hu.bme.aut.cart.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing orders within the CART module.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final BasketService basketService;
    private final ModelMapper modelMapper;
    private final CoreClient coreClient;

    /**
     * Creates an order from a specified basket.
     *
     * @param basketId The ID of the basket from which to create the order.
     * @param cardId The ID of the card used for the order.
     * @param userToken The user's token for validation.
     * @return An OrderDTO containing detailed information about the newly created order.
     * @throws IllegalStateException if an order already exists for the specified basket.
     */
    @Transactional
    public OrderDTO createOrderFromBasket(Long basketId, String cardId, String userToken) {
        log.debug("Creating order from basket ID {}", basketId);
        Basket basket = basketService.findBasketById(basketId);

        if (basket.getOrder() != null) {
            log.error("Order already exists for this basket with ID {}", basketId);
            throw new IllegalStateException("Order already exists for this basket");
        }

        validateCard(cardId, basket.getSubTotalAmount(), userToken);

        Order order = Order.builder()
                .orderDate(new Date())
                .status(OrderStatus.PENDING)
                .totalAmount(basket.getSubTotalAmount())
                .build();
        order.setBasket(basket);

        order = orderRepository.save(order);
        BasketDTO basketDTO = basketService.saveAndConvertBasket(basket);
        log.info("Order created successfully with ID {}", order.getOrderId());

        return mapToOrderDTO(order, basketDTO);
    }

    /**
     * Retrieves all orders associated with a specific user token.
     *
     * @param userToken The user's token for validation.
     * @return A list of OrderDTOs representing the orders of the specified user.
     */
    @Transactional
    public List<OrderDTO> getOrdersByUserToken(String userToken) {
        log.debug("Fetching orders for user token {}", userToken);
        Long userId = coreClient.getUserIdFromToken(userToken);
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(order -> {
                    BasketDTO basketDTO = basketService.getBasketById(order.getBasket().getBasketId());
                    return mapToOrderDTO(order, basketDTO);
                })
                .collect(Collectors.toList());
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param orderId The ID of the order to retrieve.
     * @return An OrderDTO containing detailed information about the order.
     * @throws OrderNotFoundException if no order is found with the provided ID.
     */
    @Transactional
    public OrderDTO getOrderById(Long orderId) {
        log.debug("Retrieving order with ID {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId, "3406"));
        BasketDTO basketDTO = basketService.getBasketById(order.getBasket().getBasketId());
        return mapToOrderDTO(order, basketDTO);
    }

    /**
     * Validates if the card belongs to the user and checks the balance.
     *
     * @param cardId The ID of the card to validate.
     * @param totalAmount The total amount to check against the card balance.
     * @param userToken The user's token for validation.
     */
    private void validateCard(String cardId, double totalAmount, String userToken) {
        log.debug("Validating card ID {} for amount {}", cardId, totalAmount);
        CoreValidationResponseDTO response = coreClient.validateCard(userToken, cardId, totalAmount);
        if (!response.isSuccess()) {
            log.error("Card validation failed with error: {}", response.getErrorMessage());
            if ("10100".equals(response.getErrorCode())) {
                throw new CardNotBelongToUserException(response.getErrorMessage(), response.getErrorCode());
            } else if ("10101".equals(response.getErrorCode())) {
                throw new InsufficientFundsException(response.getErrorMessage(), response.getErrorCode());
            }
        }
    }

    /**
     * Maps an Order entity to an OrderDTO while also including detailed basket information.
     *
     * @param order The order entity to map.
     * @param basketDTO The basket DTO to attach to the order DTO.
     * @return The populated OrderDTO.
     */
    private OrderDTO mapToOrderDTO(Order order, BasketDTO basketDTO) {
        OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
        orderDTO.setBasket(basketDTO);
        orderDTO.setSuccess(true);
        return orderDTO;
    }
}