package hu.bme.aut.cart.service;

import hu.bme.aut.cart.dto.ProductDTO;
import hu.bme.aut.cart.exception.ProductNotFoundException;
import hu.bme.aut.cart.repository.BasketRepository;
import hu.bme.aut.cart.client.WarehouseClient;
import hu.bme.aut.cart.model.entity.Basket;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service to manage baskets.
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class BasketService {

    private BasketRepository basketRepository;
    private WarehouseClient warehouseClient;

    /**
     * Adds a product to the basket with the specified quantity.
     *
     * @param basketId the ID of the basket
     * @param productId the ID of the product to add
     * @param quantity the quantity of the product to add
     */
    @Transactional
    public void addProductToBasket(Long basketId, Long productId, Integer quantity) {
        Basket basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new ProductNotFoundException("Basket not found", "3404"));

        ProductDTO productDTO = warehouseClient.getProductDetails(productId);
        if (productDTO == null) {
            throw new ProductNotFoundException("Product not found", "3405");
        }

        basket.addProduct(productId, productDTO.getPrice(), quantity);
        basketRepository.save(basket);
        log.info("Product {} added to basket {} with quantity {}. Updated subtotal: {}", productId, basketId, quantity, basket.getSubTotalAmount());
    }
}