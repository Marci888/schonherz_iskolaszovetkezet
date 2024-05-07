package hu.bme.aut.cart.service;

import hu.bme.aut.cart.dto.BasketDTO;
import hu.bme.aut.cart.dto.ProductDTO;
import hu.bme.aut.cart.exception.ProductNotFoundException;
import hu.bme.aut.cart.model.enums.BasketStatus;
import hu.bme.aut.cart.repository.BasketRepository;
import hu.bme.aut.cart.client.WarehouseClient;
import hu.bme.aut.cart.model.entity.Basket;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasketService {

    private final BasketRepository basketRepository;
    private final WarehouseClient warehouseClient;
    private final ModelMapper modelMapper;

    /**
     * Adds or updates a product in a basket, creating a new basket if no active basket exists.
     *
     * @param userId the ID of the user
     * @param productId the ID of the product to add
     * @param quantity the quantity of the product to add
     * @return BasketDTO representing the updated basket
     */
    @Transactional
    public BasketDTO manageBasket(Long userId, Long productId, Integer quantity) {
        Basket basket = basketRepository.findByUserIdAndBasketStatus(userId, BasketStatus.ACTIVE)
                .orElseGet(() -> createNewBasket(userId));

        ProductDTO productInfo = warehouseClient.getProductDetails(productId);
        if (productInfo == null) {
            throw new ProductNotFoundException("Product not found", "3405");
        }

        basket.addProduct(productId, productInfo.getPrice(), quantity);
        basket = basketRepository.save(basket);

        log.info("Product {} added to basket {} with quantity {}. Updated subtotal: {}", productId, basket.getBasketId(), quantity, basket.getSubTotalAmount());

        return convertToBasketDTO(basket);
    }

    @Transactional
    public BasketDTO getBasketById(Long basketId) {
        Basket basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new ProductNotFoundException("Basket not found", "3404"));
        //TODO get products from WAREHOUSE
        return convertToBasketDTO(basket);
    }

    private Basket createNewBasket(Long userId) {
        Basket newBasket = Basket.builder()
                .userId(userId)
                .basketStatus(BasketStatus.ACTIVE)
                .subTotalAmount(0.0)
                .products(new HashMap<>())
                .build();
        return basketRepository.save(newBasket);
    }

    private BasketDTO convertToBasketDTO(Basket basket) {
        BasketDTO basketDTO = modelMapper.map(basket, BasketDTO.class);
        basketDTO.setSuccess(true);
        return basketDTO;
    }
}