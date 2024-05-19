package hu.bme.aut.core.service;

import hu.bme.aut.core.exception.CoreServiceException;
import hu.bme.aut.core.model.UserToken;
import hu.bme.aut.core.repository.UserTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import hu.bme.aut.core.repository.UserRepository;

import java.util.Base64;
import java.util.Optional;
import java.util.StringTokenizer;

/**
 * Service to validate user tokens and retrieve user-related information.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserTokenValidationService {
    private final UserTokenRepository userTokenRepository;

    /**
     * Validates the provided Base64 encoded token and retrieves the user ID.
     *
     * @param encodedToken Base64 encoded token.
     * @return The user ID if the token is valid.
     * @throws CoreServiceException if the token is invalid or expired, or if the token does not belong to any user.
     */
    @Transactional
    public Long getUserIdFromToken(String encodedToken) {
        try {
            String decodedToken = new String(Base64.getDecoder().decode(encodedToken));
            StringTokenizer tokenizer = new StringTokenizer(decodedToken, "&");
            tokenizer.nextToken();
            Long userId = Long.parseLong(tokenizer.nextToken());

            Optional<UserToken> tokenOpt = userTokenRepository.findByToken(encodedToken);
            if (tokenOpt.isEmpty() || !tokenOpt.get().getUser().getUserId().equals(userId)) {
                log.error("Token validation failed. Token does not match any user or is expired.");
                throw new CoreServiceException("A felhasználói token lejárt vagy nem értelmezhető.", "10051");
            }

            log.info("User token validated successfully for user ID: {}", userId);
            return userId;
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Invalid token provided: {}", encodedToken);
            throw new CoreServiceException("A felhasználói token nem szerepel.", "10050");
        }
    }
}