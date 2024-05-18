package hu.bme.aut.core.model;

import hu.bme.aut.core.model.UserBankCard;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a User in the system.
 */
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(of = "userId")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    /**
     * The name of the user.
     */
    private String name;

    /**
     * The unique email address of the user.
     * It's marked as non-nullable and unique in the database.
     */
    @Column(nullable = false, unique = true)
    private String email;



    /**
     * A set of tokens associated with the user for authentication purposes.
     * It's a lazy-loaded one-to-many relationship.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserToken> tokens;

    /**
     * A set of bank cards associated with the user.
     * It's a lazy-loaded one-to-many relationship.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserBankCard> bankCards;



    /**
     * Adds a token to the user's set of tokens.
     * It also ensures the bidirectional relationship is maintained.
     *
     * @throws IllegalArgumentException if the token is null.
     * @param token the UserToken to be added
     */
    public void addUserToken(UserToken token) {
        if (token == null) {
            throw new IllegalArgumentException("Token cannot be null");
        }
        if (tokens == null) {
            tokens = new HashSet<>();
        }
        tokens.add(token);
        token.setUser(this);
    }

    /**
     * Adds a bank card to the user's set of bank cards.
     * It also ensures the bidirectional relationship is maintained.
     *
     * @throws IllegalArgumentException if the bank card is null.
     * @param bankCard the UserBankCard to be added
     */
    public void addUserBankCard(UserBankCard bankCard) {
        if (bankCard == null) {
            throw new IllegalArgumentException("Bank card cannot be null");
        }
        if (bankCards == null) {
            bankCards = new HashSet<>();
        }
        bankCards.add(bankCard);
        bankCard.setUser(this);
    }
}

