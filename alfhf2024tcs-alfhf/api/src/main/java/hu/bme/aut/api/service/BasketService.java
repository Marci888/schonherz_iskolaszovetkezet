package hu.bme.aut.api.service;

import hu.bme.aut.api.dto.ApiResponse;
import hu.bme.aut.api.dto.BasketDTO;
import hu.bme.aut.api.dto.ErrorResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class BasketService {

    private final RestTemplate restTemplate;
    private final ModelMapper modelMapper;
    private String cartServiceBaseUrl;

    @Value("${cart.service.url}")
    public void setCartServiceBaseUrl(String url) {
        this.cartServiceBaseUrl = url + "/basket/";
    }

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
     * @param userId    the user ID.
     * @param productId the product ID to add.
     * @param quantity  the quantity of the product to add.
     * @return CompletableFuture of ApiResponse containing the updated BasketDTO.
     */
    @Async
    public CompletableFuture<ApiResponse<BasketDTO>> addToBasket(Long userId, Long productId, Integer quantity) {
        String url = cartServiceBaseUrl + productId + "/" + quantity;
        log.debug("Adding product ID {} with quantity {} to basket for user {}", productId, quantity, userId);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                BasketDTO basketDTO = modelMapper.map(response.getBody(), BasketDTO.class);
                log.info("Product added successfully to basket for user {}", userId);
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
     * @param userId    the user ID.
     * @param productId the product ID to remove.
     * @param quantity  the quantity of the product to remove.
     * @return CompletableFuture of ApiResponse containing the updated BasketDTO.
     */
    @Async
    public CompletableFuture<ApiResponse<BasketDTO>> removeFromBasket(Long userId, Long productId, Integer quantity) {
        String url = cartServiceBaseUrl + productId + "/" + quantity;
        log.debug("Attempting to remove product ID {} with quantity {} from basket for user {}", productId, quantity, userId);
        try {
            restTemplate.delete(url);
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                BasketDTO basketDTO = modelMapper.map(response.getBody(), BasketDTO.class);
                log.info("Product removed successfully from basket for user {}", userId);
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