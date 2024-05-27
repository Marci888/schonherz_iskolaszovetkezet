package hu.bme.aut.api.servicetest;
import hu.bme.aut.api.dto.ApiResponse;
import hu.bme.aut.api.dto.BasketDTO;
import hu.bme.aut.api.dto.ErrorResponseDTO;
import hu.bme.aut.api.service.BasketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BasketServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BasketService basketService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetBasketById_Success() {
        // Arrange
        Long basketId = 1L;
        BasketDTO basketDTO = new BasketDTO();
        ApiResponse<BasketDTO> expectedResponse = new ApiResponse<>(true, null, null, basketDTO);
        ResponseEntity<String> responseEntity = new ResponseEntity<>("{\"basketId\":1}", HttpStatus.OK);
        CompletableFuture<ApiResponse<BasketDTO>> future = CompletableFuture.completedFuture(expectedResponse);

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(responseEntity);
        when(modelMapper.map(anyString(), eq(BasketDTO.class))).thenReturn(basketDTO);

        // Act
        CompletableFuture<ApiResponse<BasketDTO>> result = basketService.getBasketById(basketId);

        // Assert
        assertTrue(result.join().isSuccess());
        assertEquals(expectedResponse, result.join());
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(String.class));
        verify(modelMapper, times(1)).map(anyString(), eq(BasketDTO.class));
    }

    @Test
    public void testAddToBasket_Success() {
        // Arrange
        String userToken = "token123";
        Long productId = 1L;
        Integer quantity = 1;
        BasketDTO basketDTO = new BasketDTO();
        ApiResponse<BasketDTO> expectedResponse = new ApiResponse<>(true, null, null, basketDTO);
        ResponseEntity<String> responseEntity = new ResponseEntity<>("{\"basketId\":1}", HttpStatus.OK);
        CompletableFuture<ApiResponse<BasketDTO>> future = CompletableFuture.completedFuture(expectedResponse);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(), eq(String.class))).thenReturn(responseEntity);
        when(modelMapper.map(anyString(), eq(BasketDTO.class))).thenReturn(basketDTO);

        // Act
        CompletableFuture<ApiResponse<BasketDTO>> result = basketService.addToBasket(userToken, productId, quantity);

        // Assert
        assertTrue(result.join().isSuccess());
        assertEquals(expectedResponse, result.join());
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.PUT), any(), eq(String.class));
        verify(modelMapper, times(1)).map(anyString(), eq(BasketDTO.class));
    }

    @Test
    public void testRemoveFromBasket_Success() {
        // Arrange
        String userToken = "token123";
        Long productId = 1L;
        Integer quantity = 1;
        BasketDTO basketDTO = new BasketDTO();
        ApiResponse<BasketDTO> expectedResponse = new ApiResponse<>(true, null, null, basketDTO);
        ResponseEntity<String> responseEntity = new ResponseEntity<>("{\"basketId\":1}", HttpStatus.OK);
        CompletableFuture<ApiResponse<BasketDTO>> future = CompletableFuture.completedFuture(expectedResponse);

        doReturn(responseEntity).when(restTemplate).exchange(anyString(), eq(HttpMethod.DELETE), any(), eq(Void.class));
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(responseEntity);
        when(modelMapper.map(anyString(), eq(BasketDTO.class))).thenReturn(basketDTO);

        // Act
        CompletableFuture<ApiResponse<BasketDTO>> result = basketService.removeFromBasket(userToken, productId, quantity);

        // Assert
        assertTrue(result.join().isSuccess());
        assertEquals(expectedResponse, result.join());
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.DELETE), any(), eq(Void.class));
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(String.class));
        verify(modelMapper, times(1)).map(anyString(), eq(BasketDTO.class));
    }
}