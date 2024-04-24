package hu.bme.aut.cart.client;

import hu.bme.aut.cart.dto.ProductDTO;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Client to communicate with the WAREHOUSE module to fetch product details.
 */
@RequiredArgsConstructor
@Builder
@Component
public class WarehouseClient {

    private final RestTemplate restTemplate;

    @Value("${warehouse.service.url}")
    private final String warehouseServiceUrl;

    /**
     * Fetch product details from the WAREHOUSE service.
     *
     * @param productId the product ID to fetch
     * @return ProductDTO containing details about the product
     */
    public ProductDTO getProductDetails(Long productId) {
        URI uri = UriComponentsBuilder.fromHttpUrl(warehouseServiceUrl)
                .pathSegment("products", productId.toString())
                .build()
                .toUri();
        return restTemplate.getForObject(uri, ProductDTO.class);
    }
}