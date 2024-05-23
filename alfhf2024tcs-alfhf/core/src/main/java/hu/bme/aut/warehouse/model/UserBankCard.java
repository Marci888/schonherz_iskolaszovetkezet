package hu.bme.aut.warehouse.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a bank card associated with a User.
 */
@Entity
@Table(name = "user_bank_card")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(of = "userBankCardId")
public class UserBankCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userBankCardId;

    /**
     * The unique identifier of the bank card.
     */
    @Column(nullable = false, unique = true)
    private String cardId;

    /**
     * The number of the bank card.
     */
    private String cardNumber;

    /**
     * The CVC code of the bank card.
     */
    private String cvc;

    /**
     * The name on the bank card.
     */
    private String name;

    /**
     * The amount of funds in the bank card.
     */
    private Double amount;

    /**
     * The currency of the funds in the bank card.
     */
    private String currency;

    /**
     * The user to whom the bank card belongs.
     * It's a many-to-one relationship, with lazy loading.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}