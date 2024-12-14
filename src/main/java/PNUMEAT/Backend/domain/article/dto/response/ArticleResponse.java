package PNUMEAT.Backend.domain.article.dto.response;

import PNUMEAT.Backend.domain.article.entity.Article;
import PNUMEAT.Backend.domain.article.enums.ArticleCategory;
import java.time.LocalDateTime;

public record ArticleResponse(
    Long articleId,
    String articleTitle,
    String articleBody,
    LocalDateTime updatedDate,
    ArticleCategory articleCategory,
    String imageUrl
) {
    public static ArticleResponse of(Article article) {
        return new ArticleResponse(
            article.getArticleId(),
            article.getArticleTitle(),
            article.getArticleBody(),
            article.getUpdatedDate(),
            article.getArticleCategory(),
            article.getImages().get(0).getImageUrl()
        );
    }
}
