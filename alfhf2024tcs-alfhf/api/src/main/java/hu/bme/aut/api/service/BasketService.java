package hu.bme.aut.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.bme.aut.api.dto.ApiResponse;
import hu.bme.aut.api.dto.BasketDTO;
import hu.bme.aut.api.dto.ErrorResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasketService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${cart.service.url}")
    private String cartServiceBaseUrl;

    @Async
    public CompletableFuture<ApiResponse<BasketDTO>> getBasketByUser(String userToken) {
        log.debug("Attempting to retrieve basket for user");
        String url = cartServiceBaseUrl + "/basket";
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Token", userToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        return sendRequest(url, HttpMethod.GET, requestEntity, BasketDTO.class);
    }

    @Async
    public CompletableFuture<ApiResponse<BasketDTO>> getBasketById(Long basketId) {
        String url = cartServiceBaseUrl + "/basket/" + basketId;
        log.debug("Attempting to retrieve basket with ID: {}", basketId);
        return sendRequest(url, HttpMethod.GET, null, BasketDTO.class);
    }

    @Async
    public CompletableFuture<ApiResponse<BasketDTO>> addToBasket(String userToken, Long productId, Integer quantity) {
        String url = cartServiceBaseUrl + "/basket/" + productId + "/" + quantity;
        log.info("Adding product ID {} with quantity {} to basket for user token {}", productId, quantity, userToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Token", userToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        return sendRequest(url, HttpMethod.PUT, requestEntity, BasketDTO.class);
    }

    @Async
    public CompletableFuture<ApiResponse<BasketDTO>> removeFromBasket(String userToken, Long productId, Integer quantity) {
        String url = cartServiceBaseUrl + "/basket/" + productId + "/" + quantity;
        log.debug("Attempting to remove product ID {} with quantity {} from basket for user token {}", productId, quantity, userToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Token", userToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        return sendRequest(url, HttpMethod.DELETE, requestEntity, BasketDTO.class);
    }

    private <T> CompletableFuture<ApiResponse<T>> sendRequest(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType) {
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, method, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                if (response.getBody() != null) {
                    Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
                    if (responseMap.containsKey("data") && responseMap.get("data") != null) {
                        T data = objectMapper.convertValue(responseMap.get("data"), responseType);
                        log.info("Request to {} succeeded: {}", url, data);
                        return CompletableFuture.completedFuture(new ApiResponse<>(true, null, null, data));
                    } else {
                        T data = objectMapper.readValue(response.getBody(), responseType);
                        log.info("Request to {} succeeded: {}", url, data);
                        return CompletableFuture.completedFuture(new ApiResponse<>(true, null, null, data));
                    }
                }
                log.warn("Request to {} succeeded but response body or data is null", url);
                return CompletableFuture.completedFuture(new ApiResponse<>(false, "Response body or data is null", "1000", null));
            } else {
                ErrorResponseDTO error = objectMapper.readValue(response.getBody(), ErrorResponseDTO.class);
                log.warn("Request to {} failed: {}", url, error.getErrorMessage());
                return CompletableFuture.completedFuture(new ApiResponse<>(false, error.getErrorMessage(), error.getErrorCode(), null));
            }
        } catch (HttpClientErrorException ex) {
            ErrorResponseDTO error = null;
            try {
                error = objectMapper.readValue(ex.getResponseBodyAsString(), ErrorResponseDTO.class);
            } catch (Exception e) {
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