package hu.bme.aut.cart.service;

import hu.bme.aut.cart.dto.BasketDTO;
import hu.bme.aut.cart.dto.ProductDTO;
import hu.bme.aut.cart.exception.BasketNotFoundException;
import hu.bme.aut.cart.exception.ProductNotFoundException;
import hu.bme.aut.cart.model.enums.BasketStatus;
import hu.bme.aut.cart.repository.BasketRepository;
import hu.bme.aut.cart.client.WarehouseClient;
import hu.bme.aut.cart.model.entity.Basket;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for managing baskets within the CART module.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BasketService {

    private final BasketRepository basketRepository;
    private final WarehouseClient warehouseClient;
    private final ModelMapper modelMapper;

    /**
     * Adds or updates the quantity of a specified product in a user's active basket.
     * If no active basket exists, a new basket is created.
     *
     * @param userId    the ID of the user
     * @param productId the ID of the product to add
     * @param quantity  the quantity of the product to add
     * @return BasketDTO representing the updated state of the basket
     */
    @Transactional
    public BasketDTO addToBasket(Long userId, Long productId, Integer quantity) {
        log.debug("Attempting to add product {} to basket with quantity {}", productId, quantity);
        Basket basket = getOrCreateActiveBasket(userId);
        ProductDTO productInfo = fetchProductInfo(productId);
        basket.addProduct(productId, productInfo.getPrice(), quantity);
        return saveAndConvertBasket(basket);
    }

    /**
     * Removes a specified quantity of a product from a basket.
     *
     * @param basketId  the ID of the basket from which to remove the product
     * @param productId the ID of the product to remove
     * @param quantity  the quantity to remove
     * @return BasketDTO representing the updated state of the basket
     * @throws BasketNotFoundException if the basket cannot be found
     * @throws ProductNotFoundException if the product cannot be found in the basket
     */
    @Transactional
    public BasketDTO removeFromBasket(Long basketId, Long productId, Integer quantity) {
        log.debug("Attempting to remove product {} from basket {} with quantity {}", productId, basketId, quantity);
        Basket basket = findBasketById(basketId);
        ProductDTO productInfo = fetchProductInfo(productId);
        basket.removeProduct(productId, productInfo.getPrice(), quantity);
        return saveAndConvertBasket(basket);
    }

    /**
     * Retrieves the details of a specific basket by its ID.
     *
     * @param basketId the ID of the basket to retrieve
     * @return BasketDTO containing detailed information about the basket
     * @throws BasketNotFoundException if the basket cannot be found
     */
    @Transactional
    public BasketDTO getBasketById(Long basketId) {
        log.debug("Retrieving details for basket {}", basketId);
        return saveAndConvertBasket(findBasketById(basketId));
    }

    public Basket findBasketById(Long basketId) {
        return basketRepository.findById(basketId)
                .orElseThrow(() -> new BasketNotFoundException("Basket not found", "3405"));
    }

    public BasketDTO saveAndConvertBasket(Basket basket) {
        basket = basketRepository.save(basket);
        log.info("Basket {} updated. Subtotal: {}", basket.getBasketId(), basket.getSubTotalAmount());
        return convertToBasketDTO(basket);
    }

    private Basket getOrCreateActiveBasket(Long userId) {
        return basketRepository.findByUserIdAndBasketStatus(userId, BasketStatus.ACTIVE)
                .orElseGet(() -> createNewBasket(userId));
    }

    private ProductDTO fetchProductInfo(Long productId) {
        return warehouseClient.getProductDetails(productId);
    }

    private Basket createNewBasket(Long userId) {
        log.debug("Creating a new active basket for user {}", userId);
        return Basket.builder()
                .userId(userId)
                .basketStatus(BasketStatus.ACTIVE)
                .subTotalAmount(0.0)
                .products(new HashMap<>())
                .build();
    }

    private BasketDTO convertToBasketDTO(Basket basket) {
        List<ProductDTO> productDetails = basket.getProducts().entrySet().stream()
                .map(this::fetchProductDetailsWithQuantity)
                .collect(Collectors.toList());
        BasketDTO basketDTO = modelMapper.map(basket, BasketDTO.class);
        basketDTO.setProducts(productDetails);
        basketDTO.setSuccess(true);
        return basketDTO;
    }

    private ProductDTO fetchProductDetailsWithQuantity(Map.Entry<Long, Integer> entry) {
        ProductDTO product = fetchProductInfo(entry.getKey());
        product.setQuantity(entry.getValue());
        return product;
    }
}