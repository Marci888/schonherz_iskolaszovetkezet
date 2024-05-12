package hu.bme.aut.api.service;

import hu.bme.aut.api.dto.ApiResponse;
import hu.bme.aut.api.dto.ErrorResponseDTO;
import hu.bme.aut.api.dto.OrderDTO;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final RestTemplate restTemplate;
    private final ModelMapper modelMapper;
    private String cartServiceBaseUrl;

    @Value("${cart.service.url}")
    public void setCartServiceBaseUrl(String url) {
        this.cartServiceBaseUrl = url;
    }

    /**
     * Retrieves all orders associated with a specific user ID asynchronously.
     * Handles both successful and error responses from the CART service.
     *
     * @param userId The user ID for which to fetch orders.
     * @return CompletableFuture of ApiResponse containing a list of OrderDTOs or an error message.
     */
    @Async
    public CompletableFuture<ApiResponse<List<OrderDTO>>> getOrdersByUserId(Long userId) {
        String url = cartServiceBaseUrl + "/orders?userId=" + userId;
        log.debug("Requesting orders for user ID: {}", userId);
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                List<OrderDTO> orders = modelMapper.map(response.getBody(), List.class);
                log.info("Successfully retrieved orders for user ID: {}", userId);
                return CompletableFuture.completedFuture(new ApiResponse<>(true, null, null, orders));
            } else {
                ErrorResponseDTO error = modelMapper.map(response.getBody(), ErrorResponseDTO.class);
                log.warn("Failed to retrieve orders: {}", error.getErrorMessage());
                return CompletableFuture.completedFuture(new ApiResponse<>(false, error.getErrorMessage(), error.getErrorCode(), null));
            }
        } catch (HttpClientErrorException ex) {
            ErrorResponseDTO error = modelMapper.map(ex.getResponseBodyAsString(), ErrorResponseDTO.class);
            log.error("HTTP error while fetching orders for user ID: {}", userId, ex);
            return CompletableFuture.completedFuture(new ApiResponse<>(false, error.getErrorMessage(), error.getErrorCode(), null));
        }
    }

    /**
     * Retrieves a specific order by its ID asynchronously.
     * Handles both successful and error responses from the CART service.
     *
     * @param orderId The ID of the order to retrieve.
     * @return CompletableFuture of ApiResponse containing the OrderDTO or an error message.
     */
    @Async
    public CompletableFuture<ApiResponse<OrderDTO>> getOrderById(Long orderId) {
        String url = cartServiceBaseUrl + "/orders/" + orderId;
        log.debug("Requesting order with ID: {}", orderId);
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                OrderDTO orderDTO = modelMapper.map(response.getBody(), OrderDTO.class);
                log.info("Successfully retrieved order with ID: {}", orderId);
                return CompletableFuture.completedFuture(new ApiResponse<>(true, null, null, orderDTO));
            } else {
                ErrorResponseDTO error = modelMapper.map(response.getBody(), ErrorResponseDTO.class);
                log.warn("Failed to retrieve order: {}", error.getErrorMessage());
                return CompletableFuture.completedFuture(new ApiResponse<>(false, error.getErrorMessage(), error.getErrorCode(), null));
            }
        } catch (HttpClientErrorException ex) {
            ErrorResponseDTO error = modelMapper.map(ex.getResponseBodyAsString(), ErrorResponseDTO.class);
            log.error("HTTP error while fetching order with ID: {}", orderId, ex);
            return CompletableFuture.completedFuture(new ApiResponse<>(false, error.getErrorMessage(), error.getErrorCode(), null));
        }
    }
}