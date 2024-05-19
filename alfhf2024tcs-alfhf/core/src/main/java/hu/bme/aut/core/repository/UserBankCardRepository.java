package hu.bme.aut.core.repository;

import hu.bme.aut.core.model.UserBankCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for UserBankCard entities.
 */
@Repository
public interface UserBankCardRepository extends JpaRepository<UserBankCard, Long> {
    Optional<UserBankCard> findByCardIdAndUserId(String cardId, Long userId);
}
