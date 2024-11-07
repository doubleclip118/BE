package PNUMEAT.Backend.domain.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String token;

    private String expiration;

    protected RefreshToken() {
    }

    public RefreshToken(User user, String token, String expiration) {
        this.user = user;
        this.token = token;
        this.expiration = expiration;
    }

    public void updateToken(String newToken, String newExpiration) {
        this.token = newToken;
        this.expiration = newExpiration;
    }
}
