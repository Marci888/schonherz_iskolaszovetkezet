package hu.bme.aut.cart.client;

import hu.bme.aut.cart.dto.ProductDTO;
import hu.bme.aut.cart.exception.ProductNotFoundException;
import hu.bme.aut.cart.exception.ServiceCommunicationException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Client to communicate with the WAREHOUSE module to fetch product details.
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class WarehouseClient {

    private final RestTemplate restTemplate;

    @Value("${warehouse.service.url}")
    private final String warehouseServiceUrl;

    /**
     * Fetch product details from the WAREHOUSE service.
     *
     * @param productId the product ID to fetch
     * @return ProductDTO containing details about the product
     * @throws ProductNotFoundException if the product is not found
     * @throws ServiceCommunicationException if there is an issue communicating with the service
     */
    public ProductDTO getProductDetails(Long productId) {
        URI uri = UriComponentsBuilder.fromHttpUrl(warehouseServiceUrl)
                .pathSegment("products", productId.toString())
                .build()
                .toUri();
        log.debug("Fetching product details from URI: {}", uri);

        try {
            ResponseEntity<ProductDTO> response = restTemplate.getForEntity(uri, ProductDTO.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Product details retrieved successfully for productId: {}", productId);
                return response.getBody();
            } else {
                log.warn("Product not found for productId: {}, status code: {}", productId, response.getStatusCode());
                throw new ProductNotFoundException("Product not found: " + productId, "3404");
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.error("Product not found exception for productId: {}, error: {}", productId, e.getMessage());
            throw new ProductNotFoundException("Product not found: " + productId, "3404");
        } catch (Exception e) {
            log.error("Error communicating with warehouse service for productId: {}, error: {}", productId, e.getMessage(), e);
            throw new ServiceCommunicationException("Failed to communicate with warehouse service", "3500");
        }
    }
}