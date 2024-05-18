package hu.bme.aut.core.controller;

import hu.bme.aut.core.dto.CoreValidationResponseDTO;
import hu.bme.aut.core.service.UserBankCardValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling requests related to bank card validations and balance checks.
 * This controller provides an endpoint for validating if a card belongs to the user
 * based on a token and if the card has sufficient balance to cover a specified price.
 */
@Slf4j
@RestController
@RequestMapping("/core")
@RequiredArgsConstructor
public class BankCardController {

    private final UserBankCardValidationService userBankCardValidationService;

    /**
     * Checks if the bank card belongs to the user identified by the provided token
     * and if the card has enough balance to cover the given price.
     *
     * @param token The user authentication token provided in the request header.
     * @param cardId The unique identifier of the bank card.
     * @param price The amount to check against the card's balance.
     * @return A {@link ResponseEntity} with {@link CoreValidationResponseDTO}, indicating
     *         the result of both the card validation and the balance check.
     */
    @GetMapping("/balance/{cardId}/{price}")
    public ResponseEntity<CoreValidationResponseDTO> checkBalance(
            @RequestHeader("User-Token") String token,
            @PathVariable Long userId,
            @PathVariable String cardId,
            @PathVariable int price) {
        log.info("Request to validate card and check balance for card ID: {} and price: {}", cardId, price);
        boolean isValid = userBankCardValidationService.validateCard(token, cardId);
        boolean hasBalance = userBankCardValidationService.checkCardBalance(cardId, price);

        return ResponseEntity.ok(new CoreValidationResponseDTO(isValid && hasBalance, null, null));
    }
}