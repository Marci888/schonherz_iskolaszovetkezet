package hu.bme.aut.cart.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.bme.aut.cart.dto.CoreValidationResponseDTO;
import hu.bme.aut.cart.exception.ServiceCommunicationException;
import hu.bme.aut.cart.exception.UserTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoreClient {

    private final RestTemplate restTemplate;

    @Value("${core.service.url}")
    private String coreServiceUrl;

    /**
     * Validates if a card belongs to the user and checks the balance.
     *
     * @param userToken The user's token.
     * @param cardId The ID of the card to validate.
     * @param price The price to check against the card's balance.
     * @return CoreValidationResponseDTO containing the validation result.
     */
    public CoreValidationResponseDTO validateCard(String userToken, String cardId, double price) {
        String url = String.format("%s/core/balance/%s/%f", coreServiceUrl, cardId, price);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Token", userToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<CoreValidationResponseDTO> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, CoreValidationResponseDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            log.error("Error during card validation: {}", ex.getResponseBodyAsString());
            throw new ServiceCommunicationException("Error during card validation", "3500");
        }
    }

    /**
     * Retrieves the user ID associated with the given token.
     *
     * @param userToken The user's token.
     * @return The user ID.
     * @throws UserTokenException if the token is invalid or expired.
     */
    public Long getUserIdFromToken(String userToken) {
        String url = coreServiceUrl + "/auth";
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Token", userToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<CoreValidationResponseDTO> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, CoreValidationResponseDTO.class);
            if (Objects.requireNonNull(response.getBody()).isSuccess()) {
                return Long.parseLong(Objects.requireNonNull(response.getHeaders().getFirst("User-ID")));
            } else {
                throw new UserTokenException(response.getBody().getErrorMessage(), response.getBody().getErrorCode());
            }
        } catch (HttpClientErrorException ex) {
            log.error("Error during token validation: {}", ex.getResponseBodyAsString());
            CoreValidationResponseDTO errorResponse;
            try {
                errorResponse = new ObjectMapper().readValue(ex.getResponseBodyAsString(), CoreValidationResponseDTO.class);
            } catch (JsonProcessingException e) {
                throw new ServiceCommunicationException("Error during token validation", "3500");
            }
            throw new UserTokenException(errorResponse.getErrorMessage(), errorResponse.getErrorCode());
        }
    }
}