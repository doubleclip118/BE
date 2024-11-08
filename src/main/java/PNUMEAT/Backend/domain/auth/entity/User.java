package PNUMEAT.Backend.domain.auth.entity;


import PNUMEAT.Backend.domain.article.entity.Article;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.Getter;

@Entity
@Getter
@Table(name = "`user`")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String username;

    private String uuid;

    private String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Article> articles = new ArrayList<>();

    protected User() {
    }

    public User(String email, String username, String role) {
        this.email = email;
        this.username = username;
        this.role = role;
        this.uuid = UUID.randomUUID().toString();
    }
}
