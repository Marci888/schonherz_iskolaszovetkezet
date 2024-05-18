package hu.bme.aut.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an authentication token of a User.
 */
@Entity
@Table(name = "user_token")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userTokenId;

    /**
     * The token string used for user authentication.
     */
    private String token;

    /**
     * The user to whom the token belongs.
     * It's a many-to-one relationship, with lazy loading.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}