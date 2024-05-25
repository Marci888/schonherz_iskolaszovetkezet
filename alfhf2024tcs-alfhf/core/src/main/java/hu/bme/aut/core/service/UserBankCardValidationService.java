package hu.bme.aut.core.service;

import hu.bme.aut.core.exception.CoreServiceException;
import hu.bme.aut.core.model.UserBankCard;
import hu.bme.aut.core.repository.UserBankCardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for validating user bank cards against users and checking balance.
 * This service ensures that a bank card is associated with the correct user based on the provided user token,
 * and it checks whether the card has sufficient balance for transactions.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserBankCardValidationService {

    private final UserTokenValidationService userTokenValidationService;
    private final UserBankCardRepository userBankCardRepository;

    /**
     * Validates that the provided card ID belongs to the user identified by the given token.
     * Utilizes the UserTokenValidationService to validate the user token and get the user details.
     *
     * @param token  The user authentication token used to identify the user.
     * @param cardId The card ID that needs to be validated against the user.
     * @return true if the card belongs to the user, otherwise throws CoreServiceException.
     * @throws CoreServiceException if the token is expired or cannot be interpreted, or if the card does not belong to the user.
     */
    @Transactional
    public boolean validateCard(String token, String cardId) {
        Long userId = userTokenValidationService.getUserIdFromToken(token);
        Optional<UserBankCard> card = userBankCardRepository.findByCardIdAndUserId(cardId, userId);
        if (card.isEmpty()) {
            log.error("Card validation failed for card ID: {} and user ID: {}", cardId, userId);
            throw new CoreServiceException("Ez a bankkártya nem ehhez a felhasználóhoz tartozik.", "10100");
        }

        log.info("Card validation successful for card ID: {} and user ID: {}", cardId, userId);
        return true;
    }

    /**
     * Checks if the card identified by cardId has sufficient balance to cover the specified price.
     *
     * @param cardId The card ID for which the balance needs to be checked.
     * @param price  The amount that needs to be checked against the card's balance.
     * @return true if the card has sufficient balance, otherwise throws CoreServiceException.
     * @throws CoreServiceException if the card does not exist or does not have sufficient funds.
     */
    @Transactional
    public boolean checkCardBalance(String cardId, int price) {
        Optional<UserBankCard> cardOpt = userBankCardRepository.findById(Long.valueOf(cardId));
        if (cardOpt.isEmpty() || cardOpt.get().getAmount() < price) {
            log.error("Balance check failed for card ID: {}. Required: {}, Available: {}", cardId, price, cardOpt.map(UserBankCard::getAmount).orElse(0.0));
            throw new CoreServiceException("A felhasználónak nincs elegendő pénze hogy megvásárolja a jegyet!", "10101");
        }

        log.info("Sufficient balance available on card ID: {}", cardId);
        return true;
    }
}