package hu.bme.aut.cart.servicetest;

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
import hu.bme.aut.cart.service.BasketService;
import hu.bme.aut.cart.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private BasketService basketService;

    @Mock
    private CoreClient coreClient;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderService orderService;

    private Basket basket;
    private Order order;

    @BeforeEach
    void setUp() {
        basket = new Basket();
        basket.setBasketId(1L);
        basket.setSubTotalAmount(100.0);

        order = new Order();
        order.setOrderId(1L);
        order.setBasket(basket);
        order.setOrderDate(new Date());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(100.0);
    }

    @Test
    void testCreateOrderFromBasket_Success() {
        // Arrange
        when(coreClient.validateCard(anyString(), anyString(), anyDouble())).thenReturn(new CoreValidationResponseDTO());

        // Act
        OrderDTO result = orderService.createOrderFromBasket(1L, "card123", "token123");

        // Assert
        assertNotNull(result);
        // Add further assertions as needed
    }

    @Test
    void testCreateOrderFromBasket_OrderAlreadyExists() {
        // Arrange
        basket.setOrder(order);
        when(basketService.findBasketById(anyLong())).thenReturn(basket);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            orderService.createOrderFromBasket(1L, "card123", "token123");
        });

        assertEquals("Order already exists for this basket", exception.getMessage());
        verify(basketService, times(1)).findBasketById(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testGetOrdersByUserToken_Success() {
        // Arrange
        when(coreClient.getUserIdFromToken(anyString())).thenReturn(1L);
        when(orderRepository.findByUserId(anyLong())).thenReturn(Collections.singletonList(order));
        when(basketService.getBasketById(anyLong())).thenReturn(new BasketDTO());
        when(modelMapper.map(any(Order.class), eq(OrderDTO.class))).thenReturn(new OrderDTO());

        // Act
        var results = orderService.getOrdersByUserToken("token123");

        // Assert
        assertNotNull(results);
        assertFalse(results.isEmpty());
        verify(orderRepository, times(1)).findByUserId(anyLong());
    }

    @Test
    void testGetOrderById_Success() {
        // Arrange
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(basketService.getBasketById(anyLong())).thenReturn(new BasketDTO());
        when(modelMapper.map(any(Order.class), eq(OrderDTO.class))).thenReturn(new OrderDTO());

        // Act
        OrderDTO result = orderService.getOrderById(1L);

        // Assert
        assertNotNull(result);
        verify(orderRepository, times(1)).findById(anyLong());
    }

    @Test
    void testGetOrderById_NotFound() {
        // Arrange
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> {
            orderService.getOrderById(1L);
        });

        assertEquals("Order not found with ID: 1", exception.getMessage());
        verify(orderRepository, times(1)).findById(anyLong());
    }

    @Test
    void testValidateCard_CardNotBelongToUser() {
        // Arrange
        CoreValidationResponseDTO response = new CoreValidationResponseDTO();
        response.setSuccess(false);
        response.setErrorCode("10100");
        response.setErrorMessage("Card does not belong to user");
        when(coreClient.validateCard(anyString(), anyString(), anyDouble())).thenReturn(response);

        // Act & Assert
        CardNotBelongToUserException exception = assertThrows(CardNotBelongToUserException.class, () -> {
            orderService.validateCard("card123", 100.0, "token123");
        });

        assertEquals("Card does not belong to user", exception.getMessage());
        verify(coreClient, times(1)).validateCard(anyString(), anyString(), anyDouble());
    }

    @Test
    void testValidateCard_InsufficientFunds() {
        // Arrange
        CoreValidationResponseDTO response = new CoreValidationResponseDTO();
        response.setSuccess(false);
        response.setErrorCode("10101");
        response.setErrorMessage("Insufficient funds");
        when(coreClient.validateCard(anyString(), anyString(), anyDouble())).thenReturn(response);

        // Act & Assert
        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class, () -> {
            orderService.validateCard("card123", 100.0, "token123");
        });

        assertEquals("Insufficient funds", exception.getMessage());
        verify(coreClient, times(1)).validateCard(anyString(), anyString(), anyDouble());
    }
}
