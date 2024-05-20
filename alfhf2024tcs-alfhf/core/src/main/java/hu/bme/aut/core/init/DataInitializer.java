package hu.bme.aut.core.init;

import hu.bme.aut.core.model.User;
import hu.bme.aut.core.model.UserBankCard;
import hu.bme.aut.core.model.UserToken;
import hu.bme.aut.core.repository.UserBankCardRepository;
import hu.bme.aut.core.repository.UserRepository;
import hu.bme.aut.core.repository.UserTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserBankCardRepository userBankCardRepository;
    private final UserTokenRepository userTokenRepository;

    @Autowired
    public DataInitializer(UserRepository userRepository, UserBankCardRepository userBankCardRepository, UserTokenRepository userTokenRepository) {
        this.userRepository = userRepository;
        this.userBankCardRepository = userBankCardRepository;
        this.userTokenRepository = userTokenRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Töröljük az összes rekordot
        userRepository.deleteAll();
        userBankCardRepository.deleteAll();
        userTokenRepository.deleteAll();

        // Példányok létrehozása és hozzáadása az adatbázishoz
        User user1 = createUser("John Doe", "john@example.com");
        User user2 = createUser("Alice Smith", "alice@example.com");

        UserBankCard card1 = createBankCard("1234567890", "John Doe", "123", 1000.0, "USD", user1);
        UserBankCard card2 = createBankCard("0987654321", "Alice Smith", "456", 2000.0, "EUR", user2);

        UserToken token1 = createToken("token123", user1);
        UserToken token2 = createToken("token456", user2);

        // Mentjük az entitásokat
        userRepository.save(user1);
        userRepository.save(user2);

        userBankCardRepository.save(card1);
        userBankCardRepository.save(card2);

        userTokenRepository.save(token1);
        userTokenRepository.save(token2);
    }

    private User createUser(String name, String email) {
        return User.builder()
                .name(name)
                .email(email)
                .build();
    }

    private UserBankCard createBankCard(String cardId, String name, String cvc, Double amount, String currency, User user) {
        return UserBankCard.builder()
                .cardId(cardId)
                .name(name)
                .cvc(cvc)
                .amount(amount)
                .currency(currency)
                .user(user)
                .build();
    }

    private UserToken createToken(String token, User user) {
        return UserToken.builder()
                .token(token)
                .user(user)
                .build();
    }
}
