package PNUMEAT.Backend.domain.article.repository;

import PNUMEAT.Backend.domain.article.entity.Article;
import PNUMEAT.Backend.domain.article.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findByArticleId(Long articleId);
    List<Article> findByMemberId(Long memberId);
    List<Article> findByCategory(Category category);
}
