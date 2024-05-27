package hu.bme.aut.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.bme.aut.api.dto.ApiResponse;
import hu.bme.aut.api.dto.ErrorResponseDTO;
import hu.bme.aut.api.dto.OrderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    private final ObjectMapper objectMapper;

    @Value("${cart.service.url}")
    private String cartServiceBaseUrl;

    @Async
    public CompletableFuture<ApiResponse<List<OrderDTO>>> getOrdersByUserToken(String userToken) {
        String url = cartServiceBaseUrl + "/orders";
        log.debug("Requesting orders for user token: {}", userToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Token", userToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        return sendRequest(url, HttpMethod.GET, requestEntity, new TypeReference<List<OrderDTO>>() {});
    }

    @Async
    public CompletableFuture<ApiResponse<OrderDTO>> getOrderById(Long orderId) {
        String url = cartServiceBaseUrl + "/orders/" + orderId;
        log.debug("Requesting order with ID: {}", orderId);
        HttpEntity<Void> requestEntity = new HttpEntity<>(new HttpHeaders());
        return sendRequest(url, HttpMethod.GET, requestEntity, new TypeReference<OrderDTO>() {});
    }

    @Async
    public CompletableFuture<ApiResponse<OrderDTO>> createOrderForUser(String userToken, String cardId) {
        String url = cartServiceBaseUrl + "/orders/" + cardId;
        log.debug("Creating order for user with card ID: {}", cardId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Token", userToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        return sendRequest(url, HttpMethod.POST, requestEntity, new TypeReference<OrderDTO>() {});
    }

    private <T> CompletableFuture<ApiResponse<T>> sendRequest(String url, HttpMethod method, HttpEntity<?> requestEntity, TypeReference<T> responseType) {
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, method, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                if (response.getBody() != null) {
                    T data = objectMapper.readValue(response.getBody(), responseType);
                    log.info("Request to {} succeeded: {}", url, data);
                    return CompletableFuture.completedFuture(new ApiResponse<>(true, null, null, data));
                }
                log.warn("Request to {} succeeded but response body is null", url);
                return CompletableFuture.completedFuture(new ApiResponse<>(false, "Response body is null", "1000", null));
            } else {
                ErrorResponseDTO error = objectMapper.readValue(response.getBody(), ErrorResponseDTO.class);
                log.warn("Request to {} failed: {}", url, error.getErrorMessage());
                return CompletableFuture.completedFuture(new ApiResponse<>(false, error.getErrorMessage(), error.getErrorCode(), null));
            }
        } catch (HttpClientErrorException ex) {
            ErrorResponseDTO error = null;
            try {
                error = objectMapper.readValue(ex.getResponseBodyAsString(), ErrorResponseDTO.class);
            } catch (JsonProcessingException e) {
                log.error("Error during request to {}", url, e);
                return CompletableFuture.completedFuture(new ApiResponse<>(false, "Error during request", "1000", null));
            }
            log.error("HTTP error during request to {}: {}", url, error.getErrorMessage());
            return CompletableFuture.completedFuture(new ApiResponse<>(false, error.getErrorMessage(), error.getErrorCode(), null));
        } catch (Exception e) {
            log.error("Error during request to {}", url, e);
            return CompletableFuture.completedFuture(new ApiResponse<>(false, "Error during request", "1000", null));
        }
    }
}