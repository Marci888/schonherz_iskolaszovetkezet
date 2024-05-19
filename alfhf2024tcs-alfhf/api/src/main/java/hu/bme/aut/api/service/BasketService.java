package hu.bme.aut.api.service;

import hu.bme.aut.api.dto.ApiResponse;
import hu.bme.aut.api.dto.BasketDTO;
import hu.bme.aut.api.dto.ErrorResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

/**
 * Service class for managing baskets within the API module.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BasketService {

    private final RestTemplate restTemplate;
    private final ModelMapper modelMapper;

    @Value("${cart.service.url}")
    private String cartServiceBaseUrl;

    /**
     * Retrieves a basket by its ID asynchronously.
     *
     * @param basketId the ID of the basket to retrieve.
     * @return CompletableFuture of ApiResponse containing the BasketDTO.
     */
    @Async
    public CompletableFuture<ApiResponse<BasketDTO>> getBasketById(Long basketId) {
        String url = cartServiceBaseUrl + basketId;
        log.debug("Attempting to retrieve basket with ID: {}", basketId);
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                BasketDTO basketDTO = modelMapper.map(response.getBody(), BasketDTO.class);
                log.info("Basket retrieved successfully for ID: {}", basketId);
                return CompletableFuture.completedFuture(new ApiResponse<>(true, null, null, basketDTO));
            } else {
                ErrorResponseDTO error = modelMapper.map(response.getBody(), ErrorResponseDTO.class);
                log.warn("Failed to retrieve basket: {}", error.getErrorMessage());
                return CompletableFuture.completedFuture(new ApiResponse<>(false, error.getErrorMessage(), error.getErrorCode(), null));
            }
        } catch (HttpClientErrorException ex) {
            ErrorResponseDTO error = modelMapper.map(ex.getResponseBodyAsString(), ErrorResponseDTO.class);
            log.error("HTTP error during basket retrieval: {}", error.getErrorMessage());
            return CompletableFuture.completedFuture(new ApiResponse<>(false, error.getErrorMessage(), error.getErrorCode(), null));
        }
    }

    /**
     * Adds a product to an active basket for a given user asynchronously.
     *
     * @param userToken the user token.
     * @param productId the product ID to add.
     * @param quantity  the quantity of the product to add.
     * @return CompletableFuture of ApiResponse containing the updated BasketDTO.
     */
    @Async
    public CompletableFuture<ApiResponse<BasketDTO>> addToBasket(String userToken, Long productId, Integer quantity) {
        String url = cartServiceBaseUrl + productId + "/" + quantity;
        log.debug("Adding product ID {} with quantity {} to basket for user token {}", productId, quantity, userToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Token", userToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                BasketDTO basketDTO = modelMapper.map(response.getBody(), BasketDTO.class);
                log.info("Product added successfully to basket for user token {}", userToken);
                return CompletableFuture.completedFuture(new ApiResponse<>(true, null, null, basketDTO));
            } else {
                ErrorResponseDTO error = modelMapper.map(response.getBody(), ErrorResponseDTO.class);
                log.warn("Failed to add product to basket: {}", error.getErrorMessage());
                return CompletableFuture.completedFuture(new ApiResponse<>(false, error.getErrorMessage(), error.getErrorCode(), null));
            }
        } catch (HttpClientErrorException ex) {
            ErrorResponseDTO error = modelMapper.map(ex.getResponseBodyAsString(), ErrorResponseDTO.class);
            log.error("HTTP error when adding product to basket: {}", error.getErrorMessage());
            return CompletableFuture.completedFuture(new ApiResponse<>(false, error.getErrorMessage(), error.getErrorCode(), null));
        }
    }

    /**
     * Removes a product from an active basket for a given user asynchronously.
     *
     * @param userToken the user token.
     * @param productId the product ID to remove.
     * @param quantity  the quantity of the product to remove.
     * @return CompletableFuture of ApiResponse containing the updated BasketDTO.
     */
    @Async
    public CompletableFuture<ApiResponse<BasketDTO>> removeFromBasket(String userToken, Long productId, Integer quantity) {
        String url = cartServiceBaseUrl + productId + "/" + quantity;
        log.debug("Attempting to remove product ID {} with quantity {} from basket for user token {}", productId, quantity, userToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Token", userToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        try {
            restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Void.class);
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                BasketDTO basketDTO = modelMapper.map(response.getBody(), BasketDTO.class);
                log.info("Product removed successfully from basket for user token {}", userToken);
                return CompletableFuture.completedFuture(new ApiResponse<>(true, null, null, basketDTO));
            } else {
                ErrorResponseDTO error = modelMapper.map(response.getBody(), ErrorResponseDTO.class);
                log.warn("Failed to remove product from basket: {}", error.getErrorMessage());
                return CompletableFuture.completedFuture(new ApiResponse<>(false, error.getErrorMessage(), error.getErrorCode(), null));
            }
        } catch (HttpClientErrorException ex) {
            ErrorResponseDTO error = modelMapper.map(ex.getResponseBodyAsString(), ErrorResponseDTO.class);
            log.error("HTTP error when removing product from basket: {}", error.getErrorMessage());
            return CompletableFuture.completedFuture(new ApiResponse<>(false, error.getErrorMessage(), error.getErrorCode(), null));
        }
    }
}