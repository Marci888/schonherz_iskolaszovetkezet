package hu.bme.aut.cart.repository;

import hu.bme.aut.cart.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for {@link Order} entities.
 * This interface facilitates database operations for orders.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o JOIN o.basket b WHERE b.userId = :userId")
    List<Order> findByUserId(@Param("userId") Long userId);
}