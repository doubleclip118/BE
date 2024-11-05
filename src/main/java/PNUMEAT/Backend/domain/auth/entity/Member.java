package PNUMEAT.Backend.domain.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.Getter;

@Entity
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String username;

    private String uuid;

    private String role;

    protected Member() {
    }

    public Member(String email, String username, String role) {
        this.email = email;
        this.username = username;
        this.role = role;
        this.uuid = UUID.randomUUID().toString();
    }
}
