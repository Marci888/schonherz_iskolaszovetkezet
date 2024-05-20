package hu.bme.aut.cart.repository;

import hu.bme.aut.cart.model.entity.Basket;
import hu.bme.aut.cart.model.enums.BasketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for {@link Basket} entities.
 * This interface handles database operations related to baskets.
 */
@Repository
public interface BasketRepository extends JpaRepository<Basket, Long> {

    Optional<Basket> findByUserIdAndBasketStatus(Long userId, BasketStatus basketStatus);

}