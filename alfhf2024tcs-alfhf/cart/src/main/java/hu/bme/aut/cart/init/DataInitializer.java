package hu.bme.aut.cart.init;

import hu.bme.aut.cart.model.entity.Basket;
import hu.bme.aut.cart.model.entity.Order;
import hu.bme.aut.cart.model.enums.BasketStatus;
import hu.bme.aut.cart.model.enums.OrderStatus;
import hu.bme.aut.cart.repository.BasketRepository;
import hu.bme.aut.cart.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final BasketRepository basketRepository;
    private final OrderRepository orderRepository;

    public DataInitializer(BasketRepository basketRepository, OrderRepository orderRepository) {
        this.basketRepository = basketRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing data...");

        Basket basket = createBasket();
        Order order = createOrder(basket);

        basketRepository.save(basket);
        orderRepository.save(order);

        log.info("Data initialization complete.");
    }

    private Basket createBasket() {
        Basket basket = Basket.builder()
                .userId(1L)
                .basketStatus(BasketStatus.ACTIVE)
                .subTotalAmount(0.0)
                .products(createProductMap())
                .build();
        log.info("Basket created: {}", basket);
        return basket;
    }

    private Map<Long, Integer> createProductMap() {
        Map<Long, Integer> products = new HashMap<>();
        products.put(1L, 2);
        products.put(2L, 1);
        return products;
    }

    private Order createOrder(Basket basket) {
        Order order = Order.builder()
                .basket(basket)
                .orderDate(new Date())
                .status(OrderStatus.PENDING)
                .totalAmount(0.0)
                .build();
        log.info("Order created: {}", order);
        return order;
    }
}