package hu.bme.aut.warehouse.repository;

import hu.bme.aut.warehouse.entity.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryName(String categoryName);
    List<Product> findByCategoryId(Long id);
    Optional<Product> findById(Long Id);
    List<Product> findByNameStartingWith(String prefix);
    List<Product> findByNameContaining(String name);
    @Modifying
    @Transactional
    @Query(
            value = "update product set price = ?1 where name = ?2",
            nativeQuery = true
    )
    int updateProductPriceByName(Double price, String name);
}
