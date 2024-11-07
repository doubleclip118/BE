package PNUMEAT.Backend.domain.article.dto.request;

import PNUMEAT.Backend.domain.article.enums.Category;

public record ArticleRequest(String title,
                             String content,
                             Category category){
}
