package PNUMEAT.Backend.domain.article.dto.response;

import PNUMEAT.Backend.domain.article.entity.Article;
import PNUMEAT.Backend.domain.article.enums.Category;

public record ArticleResponse(
        Long articleId,
        String title,
        String content,
        Category category,
        String image,
        String createdAt
) {
    public static ArticleResponse of(Article article,String createdAt){
        return new ArticleResponse(article.getArticleId(), article.getTitle(), article.getContent(), article.getCategory(), article.getImage(), createdAt);
    }
}
