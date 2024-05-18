package hu.bme.aut.core.controller;

import hu.bme.aut.core.dto.CoreValidationResponseDTO;
import hu.bme.aut.core.service.UserTokenValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to handle token validation requests.
 * This controller is responsible for validating user tokens to ensure they are still valid
 * and correspond to an active user session.
 */
@Slf4j
@RestController
@RequestMapping("/core/auth")
@RequiredArgsConstructor
public class TokenValidationController {
    private final UserTokenValidationService tokenValidationService;

    /**
     * Validates the user token received in the request header.
     * @param token The user token provided in the "User-Token" request header.
     * @return A {@link ResponseEntity} containing a {@link CoreValidationResponseDTO} indicating the validation result.
     */
    @GetMapping
    public ResponseEntity<CoreValidationResponseDTO> validateToken(@RequestHeader("User-Token") String token) {
        log.info("Validating token for user authentication.");
        tokenValidationService.getUserIdFromToken(token);
        log.info("Token validation successful.");
        return ResponseEntity.ok(new CoreValidationResponseDTO(true, null, null));
    }
}