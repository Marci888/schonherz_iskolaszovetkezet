package hu.bme.aut.core.servicetest;

import hu.bme.aut.core.exception.CoreServiceException;
import hu.bme.aut.core.model.User;
import hu.bme.aut.core.model.UserBankCard;
import hu.bme.aut.core.repository.UserBankCardRepository;
import hu.bme.aut.core.service.UserBankCardValidationService;
import hu.bme.aut.core.service.UserTokenValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserBankCardValidationServiceTest {

    @Mock
    private UserTokenValidationService userTokenValidationService;

    @Mock
    private UserBankCardRepository userBankCardRepository;

    @InjectMocks
    private UserBankCardValidationService userBankCardValidationService;

    @Test
    void validateCard_WithValidCardAndToken_ShouldReturnTrue() {
        // Arrange
        String token = "valid_token";
        String cardId = "valid_card_id";
        long userId = 1L;
        UserBankCard userBankCard = UserBankCard.builder().userBankCardId(1L).cardId(cardId).user(User.builder().userId(userId).build()).build();

        when(userTokenValidationService.getUserIdFromToken(token)).thenReturn(userId);
        when(userBankCardRepository.findByCardIdAndUserId(cardId, userId)).thenReturn(Optional.of(userBankCard));

        // Act
        boolean result = userBankCardValidationService.validateCard(token, cardId);

        // Assert
        assertTrue(result);
        verify(userTokenValidationService).getUserIdFromToken(token);
        verify(userBankCardRepository).findByCardIdAndUserId(cardId, userId);
    }

    @Test
    void validateCard_WithInvalidCard_ShouldThrowException() {
        // Arrange
        String token = "valid_token";
        String cardId = "invalid_card_id";
        long userId = 1L;

        when(userTokenValidationService.getUserIdFromToken(token)).thenReturn(userId);
        when(userBankCardRepository.findByCardIdAndUserId(cardId, userId)).thenReturn(Optional.empty());

        // Act & Assert
        CoreServiceException exception = assertThrows(CoreServiceException.class, () -> userBankCardValidationService.validateCard(token, cardId));
        assertEquals("Ez a bankkártya nem ehhez a felhasználóhoz tartozik.", exception.getMessage());
        assertEquals("10100", exception.getErrorCode());

        verify(userTokenValidationService).getUserIdFromToken(token);
        verify(userBankCardRepository).findByCardIdAndUserId(cardId, userId);
    }

    @Test
    void checkCardBalance_WithSufficientBalance_ShouldReturnTrue() {
        // Arrange
        String cardId = "1";
        int price = 500;
        UserBankCard userBankCard = UserBankCard.builder().userBankCardId(1L).amount(1000.0).build();

        when(userBankCardRepository.findById(Long.valueOf(cardId))).thenReturn(Optional.of(userBankCard));

        // Act
        boolean result = userBankCardValidationService.checkCardBalance(cardId, price);

        // Assert
        assertTrue(result);
        verify(userBankCardRepository).findById(Long.valueOf(cardId));
    }

    @Test
    void checkCardBalance_WithInsufficientBalance_ShouldThrowException() {
        // Arrange
        String cardId = "1";
        int price = 1500;
        UserBankCard userBankCard = UserBankCard.builder().userBankCardId(1L).amount(1000.0).build();

        when(userBankCardRepository.findById(Long.valueOf(cardId))).thenReturn(Optional.of(userBankCard));

        // Act & Assert
        CoreServiceException exception = assertThrows(CoreServiceException.class, () -> userBankCardValidationService.checkCardBalance(cardId, price));
        assertEquals("A felhasználónak nincs elegendő pénze hogy megvásárolja a jegyet!", exception.getMessage());
        assertEquals("10101", exception.getErrorCode());

        verify(userBankCardRepository).findById(Long.valueOf(cardId));
    }

    @Test
    void checkCardBalance_WithInvalidCard_ShouldThrowException() {
        // Arrange
        String cardId = "-1";

        when(userBankCardRepository.findById(Long.valueOf(cardId))).thenReturn(Optional.empty());

        // Act & Assert
        CoreServiceException exception = assertThrows(CoreServiceException.class, () -> userBankCardValidationService.checkCardBalance(cardId, 500));
        assertEquals("A felhasználónak nincs elegendő pénze hogy megvásárolja a jegyet!", exception.getMessage());
        assertEquals("10101", exception.getErrorCode());

        verify(userBankCardRepository).findById(Long.valueOf(cardId));
    }
}
