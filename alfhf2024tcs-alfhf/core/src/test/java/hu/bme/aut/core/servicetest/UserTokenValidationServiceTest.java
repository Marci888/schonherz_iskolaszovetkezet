package hu.bme.aut.core.servicetest;


import hu.bme.aut.core.exception.CoreServiceException;
import hu.bme.aut.core.model.User;
import hu.bme.aut.core.model.UserToken;
import hu.bme.aut.core.repository.UserTokenRepository;
import hu.bme.aut.core.service.UserTokenValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;
import java.util.Optional;
import java.util.StringTokenizer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserTokenValidationServiceTest {

    @Mock
    private UserTokenRepository userTokenRepository;

    @InjectMocks
    private UserTokenValidationService userTokenValidationService;

    private String validEncodedToken;
    private String invalidEncodedToken;
    private Long userId;
    private UserToken userToken;

    @BeforeEach
    void setUp() {
        userId = 1L;
        String tokenContent = "somePrefix&" + userId;
        validEncodedToken = Base64.getEncoder().encodeToString(tokenContent.getBytes());
        invalidEncodedToken = "invalidToken";

        User user = new User();
        user.setUserId(userId);

        userToken = new UserToken();
        userToken.setToken(validEncodedToken);
        userToken.setUser(user);
    }

    @Test
    void testGetUserIdFromToken_ValidToken() {
        // Arrange
        when(userTokenRepository.findByToken(validEncodedToken)).thenReturn(Optional.of(userToken));

        // Act
        Long result = userTokenValidationService.getUserIdFromToken(validEncodedToken);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result);
        verify(userTokenRepository, times(1)).findByToken(validEncodedToken);
    }

    @Test
    void testGetUserIdFromToken_InvalidTokenFormat() {
        // Act & Assert
        CoreServiceException exception = assertThrows(CoreServiceException.class, () -> {
            userTokenValidationService.getUserIdFromToken(invalidEncodedToken);
        });

        assertEquals("A felhasználói token nem szerepel.", exception.getMessage());
        verify(userTokenRepository, never()).findByToken(anyString());
    }

    @Test
    void testGetUserIdFromToken_TokenNotFound() {
        // Arrange
        when(userTokenRepository.findByToken(validEncodedToken)).thenReturn(Optional.empty());

        // Act & Assert
        CoreServiceException exception = assertThrows(CoreServiceException.class, () -> {
            userTokenValidationService.getUserIdFromToken(validEncodedToken);
        });

        assertEquals("A felhasználói token lejárt vagy nem értelmezhető.", exception.getMessage());
        verify(userTokenRepository, times(1)).findByToken(validEncodedToken);
    }

    @Test
    void testGetUserIdFromToken_UserIdMismatch() {
        // Arrange
        User otherUser = new User();
        otherUser.setUserId(2L);
        userToken.setUser(otherUser);

        when(userTokenRepository.findByToken(validEncodedToken)).thenReturn(Optional.of(userToken));

        // Act & Assert
        CoreServiceException exception = assertThrows(CoreServiceException.class, () -> {
            userTokenValidationService.getUserIdFromToken(validEncodedToken);
        });

        assertEquals("A felhasználói token lejárt vagy nem értelmezhető.", exception.getMessage());
        verify(userTokenRepository, times(1)).findByToken(validEncodedToken);
    }
}
