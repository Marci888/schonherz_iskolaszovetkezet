package hu.bme.aut.core.init;

import hu.bme.aut.core.exception.CoreServiceException;
import hu.bme.aut.core.model.User;
import hu.bme.aut.core.model.UserBankCard;
import hu.bme.aut.core.model.UserToken;
import hu.bme.aut.core.repository.UserBankCardRepository;
import hu.bme.aut.core.repository.UserRepository;
import hu.bme.aut.core.repository.UserTokenRepository;
import hu.bme.aut.core.service.UserTokenValidationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserBankCardRepository userBankCardRepository;
    private final UserTokenValidationService tokenValidationService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Start from a clean slate");
        userRepository.deleteAll();
        userBankCardRepository.deleteAll();

        log.info("Creating new users");
        User user1 = createUser("John Doe", "john@example.com");
        User user2 = createUser("Alice Smith", "alice@example.com");

        log.info("Giving them bank cards");
        createBankCard("C0001", "5299706965433676", "John Doe", "123", 1000.0, "HUF", user1);
        createBankCard("C0002", "5390508354245119", "Alice Smith", "456", 2000.0, "HUF", user2);

        log.info("Giving them tokens");
        tokenValidationService.generateUserToken(user1);
        tokenValidationService.generateUserToken(user2);

        log.info("Data initialization complete.");
    }

    private User createUser(String name, String email) {
        User user = User.builder()
                .name(name)
                .email(email)
                .build();
        return userRepository.save(user);
    }

    private void createBankCard(String cardId, String cardNumber, String name, String cvc, Double amount, String currency, User user) {
        UserBankCard userBankCard = UserBankCard.builder()
                .cardId(cardId)
                .cardNumber(cardNumber)
                .name(name)
                .cvc(cvc)
                .amount(amount)
                .currency(currency)
                .user(user)
                .build();
        userBankCard = userBankCardRepository.save(userBankCard);

        user.addUserBankCard(userBankCard);
        userRepository.save(user);
    }
}