package hu.bme.aut.core.repository;

import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}