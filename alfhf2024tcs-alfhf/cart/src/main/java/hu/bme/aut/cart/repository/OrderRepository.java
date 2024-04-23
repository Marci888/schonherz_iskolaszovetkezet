package hu.bme.aut.cart.repository;

import hu.bme.aut.cart.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Order} entities.
 * This interface facilitates database operations for orders.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}