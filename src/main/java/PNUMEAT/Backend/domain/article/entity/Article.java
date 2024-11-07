package PNUMEAT.Backend.domain.article.entity;


import PNUMEAT.Backend.domain.article.enums.Category;
import PNUMEAT.Backend.domain.auth.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "articles")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleId;
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private String content;
    @Enumerated(EnumType.STRING)
    private Category category;
    private String image;
    private boolean deleted = false;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime modifiedAt;

    protected Article() {
    }

    public void updateArticle(String title, String content, Category category, String image) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.image = image;
    }

    public void insertimgUrl(String image){
        this.image = image;
    }

    @Builder
    public Article(String title, Member member, String content, Category category, String image, boolean deleted) {
        this.title = title;
        this.member = member;
        this.content = content;
        this.category = category;
        this.image = image;
        this.deleted = deleted;
    }
}
