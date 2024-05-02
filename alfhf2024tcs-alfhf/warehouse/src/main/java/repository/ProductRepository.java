package repository;

import entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryName(String categoryName);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE CONCAT(LOWER(:prefix), '%')")
    List<Product> findByPrefix(String prefix);

}
