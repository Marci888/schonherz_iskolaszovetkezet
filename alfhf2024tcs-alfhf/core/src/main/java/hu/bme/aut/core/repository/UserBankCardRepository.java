package hu.bme.aut.core.repository;

import hu.bme.aut.core.model.UserBankCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for UserBankCard entities.
 */
@Repository
public interface UserBankCardRepository extends JpaRepository<UserBankCard, Long> {
    @Query(value = "SELECT * FROM user_bank_card WHERE card_id = :cardId AND user_id = :userId", nativeQuery = true)
    Optional<UserBankCard> findByCardIdAndUserId(@Param("cardId") String cardId, @Param("userId") Long userId);
}