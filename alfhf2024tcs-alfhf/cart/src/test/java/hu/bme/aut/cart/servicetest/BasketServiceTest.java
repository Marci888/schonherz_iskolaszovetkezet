package hu.bme.aut.cart.servicetest;

import hu.bme.aut.cart.client.CoreClient;
import hu.bme.aut.cart.client.WarehouseClient;
import hu.bme.aut.cart.dto.BasketDTO;
import hu.bme.aut.cart.dto.ProductDTO;
import hu.bme.aut.cart.exception.BasketNotFoundException;
import hu.bme.aut.cart.model.entity.Basket;
import hu.bme.aut.cart.model.enums.BasketStatus;
import hu.bme.aut.cart.repository.BasketRepository;
import hu.bme.aut.cart.service.BasketService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class BasketServiceTest {

    @Mock
    private BasketRepository basketRepository;

    @Mock
    private WarehouseClient warehouseClient;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CoreClient coreClient;

    @InjectMocks
    private BasketService basketService;

    private Basket basket;
    private ProductDTO productDTO;
    private BasketDTO basketDTO;

    @BeforeEach
    void setUp() {
        basket = Basket.builder()
                .basketId(1L)
                .userId(1L)
                .basketStatus(BasketStatus.ACTIVE)
                .subTotalAmount(0.0)
                .products(new HashMap<>())
                .build();

        productDTO = ProductDTO.builder()
                .id(1L)
                .name("Product 1")
                .price(100.0)
                .quantity(10)
                .build();
        basketDTO = BasketDTO.builder()
                .basketId(1L)
                .basketStatus(BasketStatus.ACTIVE)
                .subtotalAmount(0.0)
                .products(new ArrayList<>())
                .build();
    }

    @Test
    void testAddToBasket() {
        // Arrange
        when(coreClient.getUserIdFromToken(anyString())).thenReturn(1L);
        when(basketRepository.findByUserIdAndBasketStatus(anyLong(), any(BasketStatus.class)))
                .thenReturn(Optional.of(basket));
        when(warehouseClient.getProductDetails(anyLong())).thenReturn(productDTO);
        when(basketRepository.save(any(Basket.class))).thenReturn(basket);
        when(modelMapper.map(any(Basket.class), eq(BasketDTO.class))).thenReturn(new BasketDTO());

        // Act
        BasketDTO result = basketService.addToBasket("token", 1L, 5);

        // Assert
        assertNotNull(result);
        verify(basketRepository, times(1)).save(any(Basket.class));
    }

    @Test
    void testRemoveFromBasket() {
        // Arrange
        basket.addProduct(1L, 100.0, 10);
        when(coreClient.getUserIdFromToken(anyString())).thenReturn(1L);
        when(basketRepository.findByUserIdAndBasketStatus(anyLong(), any(BasketStatus.class)))
                .thenReturn(Optional.of(basket));
        when(warehouseClient.getProductDetails(anyLong())).thenReturn(productDTO);
        when(basketRepository.save(any(Basket.class))).thenReturn(basket);
        when(modelMapper.map(any(Basket.class), eq(BasketDTO.class))).thenReturn(new BasketDTO());

        // Act
        BasketDTO result = basketService.removeFromBasket("token", 1L, 5);

        // Assert
        assertNotNull(result);
        verify(basketRepository, times(1)).save(any(Basket.class));
    }

    @Test
    void testGetBasketById() {
        // Arrange
        when(basketRepository.findById(anyLong())).thenReturn(Optional.of(basket));
        when(modelMapper.map(any(Basket.class), eq(BasketDTO.class))).thenReturn(basketDTO);
        when(basketRepository.save(any(Basket.class))).thenReturn(basket);

        // Act
        BasketDTO result = basketService.getBasketById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getBasketId());
        verify(basketRepository, times(1)).findById(anyLong());
        verify(modelMapper, times(1)).map(any(Basket.class), eq(BasketDTO.class));
        verify(warehouseClient, times(basket.getProducts().size())).getProductDetails(anyLong());
    }
    @Test
    void testSaveAndConvertBasket() {
        // Arrange
        when(basketRepository.save(any(Basket.class))).thenReturn(basket);
        when(modelMapper.map(any(Basket.class), eq(BasketDTO.class))).thenReturn(basketDTO);

        // Act
        BasketDTO result = basketService.saveAndConvertBasket(basket);
        
        // Assert
        assertNotNull(result);
        verify(basketRepository, times(1)).save(any(Basket.class));
        verify(modelMapper, times(1)).map(any(Basket.class), eq(BasketDTO.class));
    }

    @Test
    void testGetBasketById_NotFound() {
        // Arrange
        when(basketRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BasketNotFoundException.class, () -> basketService.getBasketById(1L));
    }

    @Test
    void testFindBasketById() {
        // Arrange
        when(basketRepository.findById(anyLong())).thenReturn(Optional.of(basket));

        // Act
        Basket result = basketService.findBasketById(1L);

        // Assert
        assertNotNull(result);
        verify(basketRepository, times(1)).findById(anyLong());
    }

    @Test
    void testFindBasketById_NotFound() {
        // Arrange
        when(basketRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BasketNotFoundException.class, () -> basketService.findBasketById(1L));
    }

}