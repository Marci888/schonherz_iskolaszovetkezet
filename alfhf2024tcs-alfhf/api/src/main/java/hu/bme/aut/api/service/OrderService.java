package hu.bme.aut.api.service;

import hu.bme.aut.api.dto.ApiResponse;
import hu.bme.aut.api.dto.ErrorResponseDTO;
import hu.bme.aut.api.dto.OrderDTO;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service class for managing orders within the API module.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final RestTemplate restTemplate;
    private final ModelMapper modelMapper;

    @Value("${cart.service.url}")
    private String cartServiceBaseUrl;

    /**
     * Retrieves all orders associated with a specific user token asynchronously.
     * Handles both successful and error responses from the CART service.
     *
     * @param userToken The user token for which to fetch orders.
     * @return CompletableFuture of ApiResponse containing a list of OrderDTOs or an error message.
     */
    @Async
    public CompletableFuture<ApiResponse<List<OrderDTO>>> getOrdersByUserToken(String userToken) {
        String url = cartServiceBaseUrl + "/orders";
        log.debug("Requesting orders for user token: {}", userToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Token", userToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                List<OrderDTO> orders = modelMapper.map(response.getBody(), new TypeToken<List<OrderDTO>>() {}.getType());
                log.info("Successfully retrieved orders for user token: {}", userToken);
                return CompletableFuture.completedFuture(new ApiResponse<>(true, null, null, orders));
            } else {
                ErrorResponseDTO error = modelMapper.map(response.getBody(), ErrorResponseDTO.class);
                log.warn("Failed to retrieve orders: {}", error.getErrorMessage());
                return CompletableFuture.completedFuture(new ApiResponse<>(false, error.getErrorMessage(), error.getErrorCode(), null));
            }
        } catch (HttpClientErrorException ex) {
            ErrorResponseDTO error = modelMapper.map(ex.getResponseBodyAsString(), ErrorResponseDTO.class);
            log.error("HTTP error while fetching orders for user token: {}", userToken, ex);
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