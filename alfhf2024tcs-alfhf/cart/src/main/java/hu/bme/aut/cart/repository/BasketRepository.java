package hu.bme.aut.cart.repository;

import hu.bme.aut.cart.model.entity.Basket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Basket} entities.
 * This interface handles database operations related to baskets.
 */
@Repository
public interface BasketRepository extends JpaRepository<Basket, Long> {

}