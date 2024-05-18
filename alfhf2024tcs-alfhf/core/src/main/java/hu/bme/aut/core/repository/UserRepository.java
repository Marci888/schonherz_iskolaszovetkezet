package hu.bme.aut.core.repository;

import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * Repository interface for User entities.
 */
public interface UserRepository extends JpaRepository<User, Long> {
}
