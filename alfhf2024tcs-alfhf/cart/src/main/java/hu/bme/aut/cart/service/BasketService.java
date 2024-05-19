package hu.bme.aut.cart.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import hu.bme.aut.cart.client.CoreClient;
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
    private final CoreClient coreClient;

    /**
     * Adds or updates the quantity of a specified product in a user's active basket.
     * If no active basket exists, a new basket is created.
     *
     * @param userToken the user's token for validation.
     * @param productId the ID of the product to add.
     * @param quantity the quantity of the product to add.
     * @return BasketDTO representing the updated state of the basket.
     */
    @Transactional
    public BasketDTO addToBasket(String userToken, Long productId, Integer quantity) {
        log.debug("Attempting to add product {} to basket with quantity {}", productId, quantity);
        Long userId = coreClient.getUserIdFromToken(userToken);
        Basket basket = getOrCreateActiveBasket(userId);
        ProductDTO productInfo = fetchProductInfo(productId);
        basket.addProduct(productId, productInfo.getPrice(), quantity);
        return saveAndConvertBasket(basket);
    }

    /**
     * Removes a specified quantity of a product from a basket.
     *
     * @param userToken the user's token for validation.
     * @param productId the ID of the product to remove.
     * @param quantity the quantity to remove.
     * @return BasketDTO representing the updated state of the basket.
     */
    @Transactional
    public BasketDTO removeFromBasket(String userToken, Long productId, Integer quantity) {
        log.debug("Attempting to remove product {} from basket with quantity {}", productId, quantity);
        Long userId = coreClient.getUserIdFromToken(userToken);
        Basket basket = getOrCreateActiveBasket(userId);
        ProductDTO productInfo = fetchProductInfo(productId);
        basket.removeProduct(productId, productInfo.getPrice(), quantity);
        return saveAndConvertBasket(basket);
    }

    /**
     * Retrieves the details of a specific basket by its ID.
     *
     * @param basketId the ID of the basket to retrieve.
     * @return BasketDTO containing detailed information about the basket.
     * @throws BasketNotFoundException if the basket cannot be found.
     */
    @Transactional
    public BasketDTO getBasketById(Long basketId) {
        log.debug("Retrieving details for basket {}", basketId);
        Basket basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new BasketNotFoundException("Basket not found", "3405"));
        return saveAndConvertBasket(basket);
    }

    /**
     * Finds a basket by its ID.
     *
     * @param basketId the ID of the basket to find.
     * @return The found Basket entity.
     */
    public Basket findBasketById(Long basketId) {
        log.debug("Finding basket by ID {}", basketId);
        return basketRepository.findById(basketId)
                .orElseThrow(() -> new BasketNotFoundException("Basket not found", "3405"));
    }

    /**
     * Saves the basket and converts it to a BasketDTO.
     *
     * @param basket The basket to save and convert.
     * @return The converted BasketDTO.
     */
    public BasketDTO saveAndConvertBasket(Basket basket) {
        basket = basketRepository.save(basket);
        log.info("Basket {} updated. Subtotal: {}", basket.getBasketId(), basket.getSubTotalAmount());
        return convertToBasketDTO(basket);
    }

    /**
     * Gets or creates an active basket for a user.
     *
     * @param userId The ID of the user.
     * @return The active Basket entity.
     */
    private Basket getOrCreateActiveBasket(Long userId) {
        log.debug("Getting or creating active basket for user ID {}", userId);
        return basketRepository.findByUserIdAndBasketStatus(userId, BasketStatus.ACTIVE)
                .orElseGet(() -> createNewBasket(userId));
    }

    /**
     * Fetches product information by its ID.
     *
     * @param productId The ID of the product to fetch.
     * @return The ProductDTO containing product information.
     */
    private ProductDTO fetchProductInfo(Long productId) {
        log.debug("Fetching product info for product ID {}", productId);
        return warehouseClient.getProductDetails(productId);
    }

    /**
     * Creates a new active basket for a user.
     *
     * @param userId The ID of the user.
     * @return The newly created Basket entity.
     */
    private Basket createNewBasket(Long userId) {
        log.debug("Creating a new active basket for user ID {}", userId);
        return Basket.builder()
                .userId(userId)
                .basketStatus(BasketStatus.ACTIVE)
                .subTotalAmount(0.0)
                .products(new HashMap<>())
                .build();
    }

    /**
     * Converts a Basket entity to a BasketDTO.
     *
     * @param basket The Basket entity to convert.
     * @return The converted BasketDTO.
     */
    private BasketDTO convertToBasketDTO(Basket basket) {
        List<ProductDTO> productDetails = basket.getProducts().entrySet().stream()
                .map(this::fetchProductDetailsWithQuantity)
                .collect(Collectors.toList());
        BasketDTO basketDTO = modelMapper.map(basket, BasketDTO.class);
        basketDTO.setProducts(productDetails);
        basketDTO.setSuccess(true);
        return basketDTO;
    }

    /**
     * Fetches product details with the specified quantity.
     *
     * @param entry The entry containing the product ID and quantity.
     * @return The ProductDTO containing product details with quantity.
     */
    private ProductDTO fetchProductDetailsWithQuantity(Map.Entry<Long, Integer> entry) {
        ProductDTO product = fetchProductInfo(entry.getKey());
        product.setQuantity(entry.getValue());
        return product;
    }
}