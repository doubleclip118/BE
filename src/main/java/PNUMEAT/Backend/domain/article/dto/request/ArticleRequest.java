package PNUMEAT.Backend.domain.article.dto.request;

import PNUMEAT.Backend.domain.article.enums.Category;
import PNUMEAT.Backend.domain.auth.entity.Member;

public record ArticleRequest(String title,
                             String content,
                             Category category,
                             String image) {
}
