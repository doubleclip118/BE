package PNUMEAT.Backend.domain.article.dto.response;

import PNUMEAT.Backend.domain.article.entity.Article;
import PNUMEAT.Backend.domain.article.enums.Category;

public record ArticleResponse(
        String title,
        String content,
        Category category,
        String image
) {
    public static ArticleResponse of(Article article){
        return new ArticleResponse(article.getTitle(), article.getContent(), article.getCategory(), article.getImage());
    }
}
